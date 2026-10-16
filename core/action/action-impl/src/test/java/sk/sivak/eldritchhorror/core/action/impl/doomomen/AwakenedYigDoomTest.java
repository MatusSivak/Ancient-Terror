package sk.sivak.eldritchhorror.core.action.impl.doomomen;

import java.lang.reflect.Proxy;
import java.util.*;
import java.util.function.BiFunction;
import org.junit.*;
import rx.Completable;
import rx.subjects.PublishSubject;
import sk.sivak.eldritchhorror.core.constants.question.*;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.action.impl.saveload.LoadAncientOneHelper;
import sk.sivak.eldritchhorror.core.constants.ancientone.*;
import sk.sivak.eldritchhorror.core.controller.*;
import sk.sivak.eldritchhorror.core.eventlistener.provider.EventListenerProvider;
import sk.sivak.eldritchhorror.core.model.*;
import sk.sivak.eldritchhorror.core.model.save.*;
import static org.junit.Assert.*;

public class AwakenedYigDoomTest {
    private int doom, power, completions;
    private boolean awake = true;
    private AncientOneId id = AncientOneId.YIG;
    private final List<String> calls = new ArrayList<>();
    private ServicePlatform platform;
    private Question<?> notice;
    private PublishSubject<Answer<Object,Object>> acknowledgement;

    @Before public void setup() {
        ServicePlatform.nullifyInstance(); platform = ServicePlatform.get();
        acknowledgement = PublishSubject.create();
        AncientOneInfo info = mock(AncientOneInfo.class, (n,a) -> {
            switch(n) {
                case "isAwaken": return awake;
                case "getAncientOneId": return id;
                case "getStartingDoom": return -8;
                case "getPower": return power;
                case "setPower": power=(int)a[0]; return null;
                default: throw new AssertionError(n);
            }
        });
        AncientOneWrite ancient = mock(AncientOneWrite.class,(n,a) -> {
            if(n.equals("getAncientOneInfo"))return info;
            if(n.equals("awaken")) { awake=true;return null; }
            if(n.equals("load"))return null;
            throw new AssertionError(n);
        });
        platform.setModel(mock(ModelWrite.class,(n,a)->ancient));
        platform.setDoomTrack(mock(DoomTrackWrite.class,(n,a)-> {
            switch(n) {
                case "getCurrentDoom":return doom;
                case "advanceDoom":return --doom;
                case "retreatDoom":return ++doom;
                case "initDoomTrack":doom=(int)a[0];return null;
                default:throw new AssertionError(n);
            }
        }));
        platform.setDoomOmenController(mock(DoomOmenController.class,(n,a)-> {
            calls.add(n+":"+a[0]+":"+a[1]);return Completable.complete();
        }));
        platform.setGameController(mock(GameController.class,(n,a)-> {
            if(n.equals("ask")) { notice=(Question<?>)a[0]; return acknowledgement.toSingle(); }
            calls.add(n+":"+doom);return null;
        }));
        platform.setEventListenerProvider(mock(EventListenerProvider.class,(n,a)-> {calls.add(n);return null;}));
        platform.setResearchEncounterDeck(mock(ResearchEncounterDeckWrite.class,(n,a)->null));
    }
    @After public void cleanup() { ServicePlatform.nullifyInstance(); }

    @Test public void awakeningResetsDoomAndRefreshesTheClockBeforeRegisteringRules() {
        awake=false;doom=-2;
        new AncientOneAwakensAction().execute().subscribe(v->completions++);
        assertFalse(awake);assertEquals(-2,doom);assertEquals(0,completions);assertTrue(calls.isEmpty());
        assertEquals("ancientOne.yig.awakening",notice.getTitle());
        assertEquals("OK",notice.getOptions().get(0).getName());
        acknowledge();
        assertEquals(-8,doom);assertEquals(1,completions);
        assertEquals(Arrays.asList("updateDoom:-8","registerAncientOneListeners"),calls);
    }

    @Test public void earlyAwakeningAlwaysSetsFortyMinutesBeforeMidnightAfterConfirmation() {
        for(int startingDoom:new int[]{1,5,8,10}) {
            setup(); calls.clear(); completions=0; awake=false; doom=startingDoom;
            new AncientOneAwakensAction().execute().subscribe(v->completions++);
            assertEquals(startingDoom,doom);assertFalse(awake);assertEquals(0,completions);
            assertEquals("ancientOne.yig.awakening",notice.getTitle());
            acknowledge();
            assertEquals(-8,doom);assertTrue(awake);assertEquals(1,completions);
            assertEquals(Arrays.asList("updateDoom:-8","registerAncientOneListeners"),calls);
        }
    }

    @Test public void repeatedAwakeningDoesNotResetAnOngoingCountdown() {
        awake=true;doom=-3;
        new AncientOneAwakensAction().execute().subscribe(v->completions++);
        assertEquals(-3,doom);assertNull(notice);assertTrue(calls.isEmpty());assertEquals(1,completions);
    }

    private void acknowledge() {
        acknowledgement.onNext(new Answer<>()); acknowledgement.onCompleted();
    }
    @Test public void advancesCountTowardMidnightAndClampWithoutOverflow() {
        doom=-8;advance(3);assertEquals(-5,doom);
        advance(9);assertEquals(0,doom);assertEquals(2,completions);
        assertEquals(Arrays.asList("advanceDoom:5:-3","advanceDoom:0:-5"),calls);
    }
    @Test public void retreatAddsFiveMinutesAndZeroAdvanceDoesNothing() {
        doom=-8;advance(0);assertEquals(-8,doom);assertTrue(calls.isEmpty());
        new RetreatDoomAction().execute().subscribe(v->completions++);
        assertEquals(-9,doom);assertEquals(Collections.singletonList("retreatDoom:9:1"),calls);
    }
    @Test public void sleepingYigAndOtherAncientOnesRetainTheirDoomDirection() {
        awake=false;doom=10;advance(2);assertEquals(8,doom);
        awake=true;id=AncientOneId.CTHULHU;doom=3;advance(1);assertEquals(2,doom);
        new RetreatDoomAction().execute().subscribe();assertEquals(3,doom);
    }
    @Test public void loadingOldTokensPreservesTimeAndDoesNotResetNewSaves() {
        power=5;doom=0;load();assertEquals(-5,doom);assertEquals(0,power);
        doom=-3;load();assertEquals(-3,doom);
        doom=0;load();assertEquals(0,doom);
        assertFalse(calls.contains("registerAncientOneListeners"));
    }
    private void load() {
        AncientOneSaveDataRead ancient=mock(AncientOneSaveDataRead.class,(n,a)->id);
        LoadAncientOneHelper.loadAncientOne(mock(SaveDataRead.class,(n,a)->ancient));
    }
    private void advance(int steps) {
        AdvanceDoomAction action=new AdvanceDoomAction();action.setInput(steps);
        action.execute().subscribe(v->completions++);
    }
    private <T>T mock(Class<T> type,BiFunction<String,Object[],Object> handler) {
        return type.cast(Proxy.newProxyInstance(type.getClassLoader(),new Class[]{type},
                (p,m,a)->handler.apply(m.getName(),a)));
    }
}
