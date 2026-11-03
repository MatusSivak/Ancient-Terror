package sk.sivak.eldritchhorror.core.eventlistener.mythos;

import java.lang.reflect.Proxy;
import java.util.*;
import java.util.function.BiFunction;
import org.junit.*;
import rx.Single;
import sk.sivak.eldritchhorror.core.constants.RumorCardInfo;
import sk.sivak.eldritchhorror.core.constants.asset.MonsterHunterAsset;
import sk.sivak.eldritchhorror.core.constants.condition.*;
import sk.sivak.eldritchhorror.core.constants.investigator.*;
import sk.sivak.eldritchhorror.core.constants.monster.*;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.monster.*;
import sk.sivak.eldritchhorror.core.eventqueue.EventQueueImpl;
import sk.sivak.eldritchhorror.core.eventtype.DirectEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.ReckoningFireType;
import sk.sivak.eldritchhorror.core.eventtype.data.test.TestData;
import sk.sivak.eldritchhorror.core.model.*;
import sk.sivak.eldritchhorror.core.service.*;
import static org.junit.Assert.*;

public class RandomInvestigatorEffectsTest {
    private final List<Runnable> pending = new ArrayList<>();
    private final List<InvestigatorId> recipients = new ArrayList<>();
    private final List<InvestigatorId> counted = new ArrayList<>();
    private final List<Integer> countdowns = new ArrayList<>();
    private final List<InvestigatorRead> eligible = new ArrayList<>();
    private InvestigatorId active;
    private int draws;
    private EventQueueImpl events;

    @Before public void setup() {
        ServicePlatform p = ServicePlatform.get();
        events = new EventQueueImpl(); p.setEventQueue(events);
        eligible.add(investigator(InvestigatorId.THE_SPY));
        eligible.add(investigator(InvestigatorId.THE_SAILOR));
        p.setInvestigators(mock(InvestigatorsRead.class, (name, args) -> {
            switch (name) {
                case "getRandomInvestigator":
                    int index = draws++;
                    return eligible.isEmpty() ? null : eligible.get(index % eligible.size());
                case "getOnBoardInvestigators": return eligible;
                case "getDefeatedInvestigators": return Collections.emptyList();
                case "getActiveInvestigatorId": return active;
                case "getActiveInvestigator": return active == null ? null : investigator(active);
                case "getInvestigator": return investigator((InvestigatorId) args[0]);
                default: throw new AssertionError(name);
            }
        }));
        p.setService(mock(Service.class, (name, args) -> {
            if (name.equals("addEventCommand")) pending.add(() ->
                    ((java8.features.function.Consumer<Object>) args[0]).accept(null));
            return null;
        }));
        p.setInvestigatorService(mock(InvestigatorService.class, (name, args) -> {
            if (name.equals("changeActiveInvestigator")) active = (InvestigatorId) args[0];
            if (name.equals("unsetActiveInvestigator")) active = null;
            return null;
        }));
        p.setGameService(mock(GameService.class, (name, args) -> {
            if (name.startsWith("gain")) recipients.add(active);
            return null;
        }));
        TestData testResult = new TestData();
        testResult.setDiceRolls(Collections.emptyList());
        p.setTestService(mock(TestService.class, (name, args) ->
                name.equals("rollDie") ? Single.just(6) : Single.just(testResult)));
        p.setMonsterCup(mock(MonsterCupRead.class, (name, args) -> Collections.emptyList()));
        p.setMonsterService(mock(MonsterService.class, (name, args) ->
                name.equals("selectSingleMonster") ? Single.just(null) : null));
        p.setAssetDeck(mock(AssetDeckRead.class, (name, args) -> new MonsterHunterAsset()));
        p.setDoomOmenService(mock(DoomOmenService.class, (name, args) -> {
            if (name.equals("countdownRumorCard")) countdowns.add((Integer) args[1]);
            return null;
        }));
        p.setConditionsDeck(mock(ConditionsDeckRead.class, (name, args) -> {
            counted.add((InvestigatorId) args[0]);
            return Collections.singletonList(mock(ConditionInfo.class, (n, a) -> null));
        }));
        p.setRumors(mock(RumorsRead.class, (name, args) ->
                mock(RumorCardInfo.class, (n, a) -> n.equals("getTimeRemaining") ? 10 : null)));
    }

    @After public void cleanup() { ServicePlatform.nullifyInstance(); }

    private List<MythosCardEventListener> cards() {
        return Arrays.asList(new GreenMythosCard3(), new GreenMythosCard7(), new GreenMythosCard8(),
                new GreenMythosCard9(), new GreenMythosCard13(), new YellowMythosCard9());
    }

    @Test public void everyAffectedMythosEffectDrawsOncePerExecution() {
        for (MythosCardEventListener card : cards()) {
            for (int i = 0; i < 2; i++) {
                int before = draws;
                card.execute(); drain();
                assertEquals(before + 1, draws);
                assertEquals(eligible.get(before % 2).getInfo().getInvestigatorId(), active);
            }
        }
    }

    @Test public void effectsSkipWhenNoInvestigatorIsEligible() {
        eligible.clear();
        for (MythosCardEventListener card : cards()) card.execute();
        new WraithMonsterListener().register(new WraithMonster());
        new PoltergeistMonsterListener().register(new PoltergeistMonster());
        new BlueMythosCard5().justLoad();
        events.fireDirectEvent(DirectEvent.RECKONING_RUMORS, ReckoningFireType.FIRE);
        drain();
        assertNull(active); assertTrue(recipients.isEmpty()); assertTrue(counted.isEmpty());
    }

    @Test public void narratedOccupationAndRecipientMatchWithoutDrawingAgain() throws Exception {
        for (MythosCardEventListener effect : cards()) {
            sk.sivak.eldritchhorror.core.constants.mythos.MythosCard card =
                    (sk.sivak.eldritchhorror.core.constants.mythos.MythosCard) Class.forName(
                            "sk.sivak.eldritchhorror.core.constants.mythos." + effect.getClass().getSimpleName())
                            .newInstance();
            assertTrue(card.requiresRandomInvestigator());
            for (InvestigatorId target : new InvestigatorId[]{InvestigatorId.THE_ASTRONOMER, InvestigatorId.THE_NUN}) {
                String text = String.join(" ", card.getInfos(target));
                assertTrue(text, text.contains("The " + target));
                assertFalse(text, text.toLowerCase(java.util.Locale.ROOT).contains("random investigator"));
                assertFalse(text, text.contains("{investigator}"));
                effect.execute(target); drain();
                assertEquals(target, active);
                assertEquals(0, draws);
            }
            active = null;
            effect.execute(null); drain();
            assertNull(active);
            assertEquals(0, draws);
        }
    }

    @Test public void eachQueuedSpawnDrawsAtResolutionAndRestoresActiveInvestigator() {
        active = InvestigatorId.THE_NUN;
        new WraithMonsterListener().register(new WraithMonster());
        new PoltergeistMonsterListener().register(new PoltergeistMonster());
        assertEquals(0, draws);
        drain();
        assertEquals(2, draws);
        assertEquals(Arrays.asList(InvestigatorId.THE_SPY, InvestigatorId.THE_SAILOR), recipients);
        assertEquals(InvestigatorId.THE_NUN, active);
    }

    @Test public void eachRumorReckoningDrawsAgainAndCountsTheSameRecipient() {
        new BlueMythosCard5().justLoad();
        events.fireDirectEvent(DirectEvent.RECKONING_RUMORS, ReckoningFireType.CHARGE);
        assertEquals(0, draws);
        for (int i = 0; i < 2; i++) {
            events.fireDirectEvent(DirectEvent.RECKONING_RUMORS, ReckoningFireType.FIRE);
            assertEquals(i, draws);
            drain();
        }
        assertEquals(2, draws);
        assertEquals(Arrays.asList(InvestigatorId.THE_SPY, InvestigatorId.THE_SAILOR), recipients);
        assertEquals(recipients, counted);
        assertEquals(Arrays.asList(1, 1), countdowns);
    }

    private void drain() { while (!pending.isEmpty()) pending.remove(0).run(); }
    private InvestigatorRead investigator(InvestigatorId id) {
        return mock(InvestigatorRead.class, (name, args) -> name.equals("getInfo")
                ? mock(InvestigatorInfo.class, (n, a) -> id) : null);
    }
    private static <T> T mock(Class<T> type, BiFunction<String, Object[], Object> handler) {
        return type.cast(Proxy.newProxyInstance(type.getClassLoader(), new Class[]{type},
                (p, method, args) -> handler.apply(method.getName(), args)));
    }
}
