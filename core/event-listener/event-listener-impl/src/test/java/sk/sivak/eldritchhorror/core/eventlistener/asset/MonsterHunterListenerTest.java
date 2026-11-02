package sk.sivak.eldritchhorror.core.eventlistener.asset;

import java.lang.reflect.Proxy;
import java.util.*;
import org.junit.After;
import org.junit.Test;
import rx.Single;
import rx.subjects.PublishSubject;
import sk.sivak.eldritchhorror.core.constants.asset.MonsterHunterAsset;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.constants.monster.*;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventqueue.EventQueueImpl;
import sk.sivak.eldritchhorror.core.eventtype.*;
import sk.sivak.eldritchhorror.core.eventtype.data.CollectAvailableActionsData;
import sk.sivak.eldritchhorror.core.model.*;
import sk.sivak.eldritchhorror.core.service.*;
import static org.junit.Assert.*;

public class MonsterHunterListenerTest {
    @After public void cleanup() { ServicePlatform.nullifyInstance(); }

    @Test public void damagesOnlyTheSelectedMonsterAfterSelectionCompletes() {
        ServicePlatform platform = ServicePlatform.get();
        EventQueueImpl events = new EventQueueImpl();
        platform.setEventQueue(events);
        InvestigatorRead owner = mock(InvestigatorRead.class, (name, args) -> {
            if (name.equals("getCurrentLocationId")) return LocationId.ROME;
            if (name.equals("isLostInTimeAndSpace")) return false;
            throw new AssertionError(name);
        });
        platform.setInvestigators(mock(InvestigatorsRead.class, (name, args) ->
                name.equals("getActiveInvestigatorId") ? InvestigatorId.THE_SAILOR : owner));
        List<MonsterInfo> monsters = Arrays.asList(new ZombieMonster(), new CultistMonster());
        platform.setMonsterCup(mock(MonsterCupRead.class, (name, args) -> {
            assertEquals("getMonstersAtLocation", name);
            assertEquals(LocationId.ROME, args[0]);
            return monsters;
        }));
        PublishSubject<MonsterInfo> selection = PublishSubject.create();
        List<MonsterInfo> damaged = new ArrayList<>();
        platform.setMonsterService(mock(MonsterService.class, (name, args) -> {
            if (name.equals("selectSingleMonster")) {
                assertEquals(monsters, ((SelectMonsterData) args[0]).getAvailableMonsters());
                return selection.toSingle();
            }
            assertEquals("dealDamageToMonster", name);
            assertEquals(1, args[1]);
            damaged.add((MonsterInfo) args[0]);
            return null;
        }));
        platform.setGameService(mock(GameService.class, (name, args) -> {
            assertEquals("showCard", name);
            return Single.just(null);
        }));
        List<String> serviceCalls = new ArrayList<>();
        platform.setService(mock(Service.class, (name, args) -> {
            serviceCalls.add(name);
            return null;
        }));
        MonsterHunterListener listener = new MonsterHunterListener();
        listener.registerWithOwner(InvestigatorId.THE_SAILOR, new MonsterHunterAsset());
        CollectAvailableActionsData actions = new CollectAvailableActionsData();
        events.fireBeforeEvent(BeforeAfterEvent.FIND_ACTIONS, actions);
        assertEquals(1, actions.getActionPhaseActions().size());
        actions.getActionPhaseActions().get(0).execute();
        assertTrue(damaged.isEmpty());
        assertEquals(Arrays.asList("hold", "disableCard", "release"), serviceCalls);
        selection.onNext(monsters.get(1));
        selection.onCompleted();
        assertEquals(Collections.singletonList(monsters.get(1)), damaged);
        assertEquals(Arrays.asList("hold", "disableCard", "release", "hold", "convertToNull", "release"), serviceCalls);
    }

    private interface Handler { Object call(String name, Object[] args); }
    private static <T> T mock(Class<T> type, Handler handler) {
        return type.cast(Proxy.newProxyInstance(type.getClassLoader(), new Class[]{type},
                (proxy, method, args) -> handler.call(method.getName(), args)));
    }
}
