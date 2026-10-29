package sk.sivak.eldritchhorror.core.eventlistener.monster;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import rx.Single;
import sk.sivak.eldritchhorror.core.constants.clue.ClueInfo;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorInfo;
import sk.sivak.eldritchhorror.core.constants.location.FindNearestData;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.constants.monster.AbstractMonsterInfo;
import sk.sivak.eldritchhorror.core.constants.monster.HoundOfTindalosMonster;
import sk.sivak.eldritchhorror.core.constants.monster.MiGoMonster;
import sk.sivak.eldritchhorror.core.constants.monster.NightGauntMonster;
import sk.sivak.eldritchhorror.core.constants.monster.WarlockMonster;
import sk.sivak.eldritchhorror.core.constants.monster.WerewolfMonster;
import sk.sivak.eldritchhorror.core.constants.monster.ColourOutOfSpaceMonster;
import sk.sivak.eldritchhorror.core.constants.monster.CthonianMonster;
import sk.sivak.eldritchhorror.core.constants.monster.ElderThingMonster;
import sk.sivak.eldritchhorror.core.constants.monster.GiantPenguinMonster;
import sk.sivak.eldritchhorror.core.constants.monster.GnophKehMonster;
import sk.sivak.eldritchhorror.core.constants.monster.HonoredDeadMonster;
import sk.sivak.eldritchhorror.core.constants.monster.HuntingByakheeMonster;
import sk.sivak.eldritchhorror.core.constants.monster.MummyMonster;
import sk.sivak.eldritchhorror.core.constants.monster.SerpentPeopleMonster;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventqueue.EventQueueImpl;
import sk.sivak.eldritchhorror.core.eventtype.DirectEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.ReckoningFireType;
import sk.sivak.eldritchhorror.core.model.CluePoolRead;
import sk.sivak.eldritchhorror.core.model.InvestigatorRead;
import sk.sivak.eldritchhorror.core.model.InvestigatorsRead;
import sk.sivak.eldritchhorror.core.model.LocationMapRead;
import sk.sivak.eldritchhorror.core.service.DoomOmenService;
import sk.sivak.eldritchhorror.core.service.GameService;
import sk.sivak.eldritchhorror.core.service.InvestigatorService;
import sk.sivak.eldritchhorror.core.service.MonsterService;
import sk.sivak.eldritchhorror.core.service.Service;
import sk.sivak.eldritchhorror.core.service.TestService;
import sk.sivak.eldritchhorror.core.service.TokenService;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static sk.sivak.eldritchhorror.core.constants.location.LocationId.SPACE_1;
import static sk.sivak.eldritchhorror.core.constants.location.LocationId.SPACE_2;
import static sk.sivak.eldritchhorror.core.constants.location.LocationId.SPACE_3;
import static sk.sivak.eldritchhorror.core.constants.location.LocationId.SPACE_4;
import static sk.sivak.eldritchhorror.core.constants.location.LocationId.SPACE_5;

/** Monster reckonings dispatched through the real event queue; service calls are recorded. */
public class MonsterListenersTest {
    private EventQueueImpl events;
    private List<String> effects;
    private List<Runnable> pending;
    private List<InvestigatorRead> onBoard;
    private List<ClueInfo> clues;
    private FindNearestData nearest;
    private int dieRoll;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() {
        ServicePlatform.nullifyInstance();
        ServicePlatform platform = ServicePlatform.get();
        events = new EventQueueImpl();
        platform.setEventQueue(events);
        effects = new ArrayList<>();
        pending = new ArrayList<>();
        onBoard = new ArrayList<>();
        clues = new ArrayList<>();
        dieRoll = 6;

        platform.setService(mock(Service.class, (name, a) -> {
            if (name.equals("addEventCommand")) {
                pending.add(() -> ((java8.features.function.Consumer<Object>) a[0]).accept(null));
            } else if (!name.equals("hold") && !name.equals("release") && !name.startsWith("convert")) {
                effects.add(name);
            }
            return null;
        }));
        platform.setGameService(mock(GameService.class, (name, a) -> {
            if (!name.equals("checkIfSomeoneIsDefeated")) effects.add(name + (a == null ? "" : ":" + a[0]));
            return null;
        }));
        platform.setDoomOmenService(mock(DoomOmenService.class, (name, a) -> {
            effects.add(name);
            return null;
        }));
        platform.setTokenService(mock(TokenService.class, (name, a) -> {
            effects.add(name + (a == null ? "" : ":" + a[0]));
            return null;
        }));
        platform.setMonsterService(mock(MonsterService.class, (name, a) -> {
            effects.add(name + (name.equals("moveMonster") ? ":" + a[1] : ""));
            if (name.equals("moveMonster")) ((AbstractMonsterInfo) a[0]).setCurrentLocation((LocationId) a[1]);
            return null;
        }));
        platform.setInvestigatorService(mock(InvestigatorService.class, (name, a) -> {
            effects.add(name + (name.equals("changeActiveInvestigator") ? ":" + a[0] : ""));
            return null;
        }));
        platform.setTestService(mock(TestService.class, (name, a) -> {
            effects.add(name);
            return name.equals("rollDie") ? Single.just(dieRoll) : null;
        }));
        platform.setInvestigators(mock(InvestigatorsRead.class, (name, a) -> {
            if (name.equals("getOnBoardInvestigators")) return onBoard;
            if (name.equals("getActiveInvestigatorId")) return null;
            throw new AssertionError(name);
        }));
        platform.setLocationMap(mock(LocationMapRead.class, (name, a) -> {
            if (name.equals("findNearest")) return nearest;
            throw new AssertionError(name);
        }));
        platform.setCluePoolRead(mock(CluePoolRead.class, (name, a) -> {
            if (name.equals("getSpawnedClues")) return clues;
            throw new AssertionError(name);
        }));
    }

    @After
    public void tearDown() {
        ServicePlatform.nullifyInstance();
    }

    @Test
    public void fixedSpawnDestinationsMoveWithoutOpeningTheMonsterSheet() {
        AbstractMonsterListener[] listeners = {new CthonianMonsterListener(), new ElderThingMonsterListener(),
                new MummyMonsterListener(), new ColourOutOfSpaceMonsterListener(), new GnophKehMonsterListener(),
                new SerpentPeopleMonsterListener(), new GiantPenguinMonsterListener(), new HonoredDeadMonsterListener()};
        AbstractMonsterInfo[] monsters = {new CthonianMonster(), new ElderThingMonster(), new MummyMonster(),
                new ColourOutOfSpaceMonster(), new GnophKehMonster(), new SerpentPeopleMonster(),
                new GiantPenguinMonster(), new HonoredDeadMonster()};
        LocationId[] targets = {LocationId.THE_HEART_OF_AFRICA, LocationId.ANTARCTICA, LocationId.THE_PYRAMIDS,
                LocationId.TUNGUSKA, LocationId.THE_HIMALAYAS, LocationId.THE_AMAZON,
                LocationId.ANTARCTICA, LocationId.THE_PYRAMIDS};
        for (int i = 0; i < listeners.length; i++) {
            effects.clear();
            listeners[i].register(monster(monsters[i], SPACE_1));
            drain();
            assertEquals(targets[i], monsters[i].getCurrentLocation());
            assertEquals(Collections.singletonList("moveMonster:" + targets[i]), effects);
        }
    }

    @Test
    public void huntingByakheeMovesAndDelaysWithoutOpeningTheMonsterSheet() {
        InvestigatorId target = InvestigatorId.values()[0];
        investigator(target, SPACE_2);
        nearest = new FindNearestData(SPACE_2, 1, Arrays.asList(SPACE_1, SPACE_2));
        HuntingByakheeMonster monster = monster(new HuntingByakheeMonster(), SPACE_1);
        new HuntingByakheeMonsterListener().register(monster);
        drain();
        assertEquals(SPACE_2, monster.getCurrentLocation());
        assertTrue(effects.contains("becomeDelayed"));
        assertFalse(effects.contains("highlightSpawnText"));
        assertTrue(effects.indexOf("moveMonster:" + SPACE_2) < effects.indexOf("becomeDelayed"));
    }

    @Test
    public void werewolfStepsTowardDistantInvestigatorWithoutDamage() {
        new WerewolfMonsterListener().register(monster(new WerewolfMonster(), SPACE_1));
        investigator(InvestigatorId.values()[0], SPACE_3);
        nearest = new FindNearestData(SPACE_3, 2, Arrays.asList(SPACE_1, SPACE_2, SPACE_3));

        fireReckoning();

        assertEquals(Arrays.asList("onTriggeredReckoning", "moveCameraToLocation", "highlightReckoningText",
                "moveMonster:" + SPACE_2), effects);
    }

    @Test
    public void werewolfDamagesEveryInvestigatorOnTheSpaceItEnters() {
        new WerewolfMonsterListener().register(monster(new WerewolfMonster(), SPACE_1));
        InvestigatorId first = InvestigatorId.values()[0];
        InvestigatorId second = InvestigatorId.values()[1];
        investigator(first, SPACE_2);
        investigator(second, SPACE_2);
        nearest = new FindNearestData(SPACE_2, 1, Arrays.asList(SPACE_1, SPACE_2));

        fireReckoning();

        assertTrue(effects.contains("moveMonster:" + SPACE_2));
        assertEquals(2, Collections.frequency(effects, "loseHealth:1"));
        assertTrue(effects.contains("changeActiveInvestigator:" + first));
        assertTrue(effects.contains("changeActiveInvestigator:" + second));
    }

    @Test
    public void werewolfReckoningNeedsChargeAndStopsAfterUnregister() {
        WerewolfMonsterListener listener = new WerewolfMonsterListener();
        listener.register(monster(new WerewolfMonster(), SPACE_1));
        investigator(InvestigatorId.values()[0], SPACE_2);
        nearest = new FindNearestData(SPACE_2, 1, Arrays.asList(SPACE_1, SPACE_2));

        events.fireDirectEvent(DirectEvent.RECKONING_MONSTER, ReckoningFireType.FIRE);
        drain();
        assertTrue(effects.isEmpty());

        listener.unregister();
        fireReckoning();
        assertTrue(effects.isEmpty());
    }

    @Test
    public void werewolfWithoutInvestigatorsDoesNothing() {
        new WerewolfMonsterListener().register(monster(new WerewolfMonster(), SPACE_1));
        fireReckoning();
        assertTrue(effects.isEmpty());
    }

    @Test
    public void houndAttacksNearestInvestigator() {
        new HoundOfTindalosMonsterListener().register(monster(new HoundOfTindalosMonster(), SPACE_1));
        InvestigatorId target = InvestigatorId.values()[0];
        investigator(target, SPACE_3);
        nearest = new FindNearestData(SPACE_3, 2, Arrays.asList(SPACE_1, SPACE_2, SPACE_3));

        fireReckoning();

        assertEquals(Arrays.asList("onTriggeredReckoning", "moveCameraToLocation", "highlightReckoningText",
                "moveMonster:" + SPACE_3, "changeActiveInvestigator:" + target,
                "showActiveInvestigator", "combat"), effects);
    }

    @Test
    public void houndIgnoresUnreachableInvestigators() {
        new HoundOfTindalosMonsterListener().register(monster(new HoundOfTindalosMonster(), SPACE_1));
        investigator(InvestigatorId.values()[0], SPACE_3);
        nearest = null;
        fireReckoning();
        assertTrue(effects.isEmpty());
    }

    @Test
    public void nightGauntMovesTwoSpacesAndToleratesUnreachableInvestigators() {
        new NightGauntMonsterListener().register(monster(new NightGauntMonster(), SPACE_1));
        investigator(InvestigatorId.values()[0], SPACE_4);
        nearest = new FindNearestData(SPACE_4, 3, Arrays.asList(SPACE_1, SPACE_2, SPACE_3, SPACE_4));
        fireReckoning();
        assertTrue(effects.contains("moveMonster:" + SPACE_3));

        effects.clear();
        nearest = null;
        fireReckoning();
        assertFalse(effects.stream().anyMatch(effect -> effect.startsWith("moveMonster")));
    }

    @Test
    public void miGoWithUnreachableClueDoesNothing() {
        new MiGoMonsterListener().register(monster(new MiGoMonster(), SPACE_1));
        clues.add(mock(ClueInfo.class, (name, a) -> SPACE_5));
        nearest = null;
        fireReckoning();
        assertTrue(effects.isEmpty());
    }

    @Test
    public void miGoMovesToNearestClueAndDiscardsIt() {
        new MiGoMonsterListener().register(monster(new MiGoMonster(), SPACE_1));
        clues.add(mock(ClueInfo.class, (name, a) -> SPACE_5));
        nearest = new FindNearestData(SPACE_5, 1, Arrays.asList(SPACE_1, SPACE_5));
        fireReckoning();
        assertTrue(effects.contains("moveMonster:" + SPACE_5));
        assertTrue(effects.contains("discardClue:" + SPACE_5));
    }

    @Test
    public void warlockCursesNearestInvestigatorOnLowRollAndToleratesUnreachable() {
        new WarlockMonsterListener().register(monster(new WarlockMonster(), SPACE_1));
        InvestigatorId target = InvestigatorId.values()[0];
        investigator(target, SPACE_2);
        dieRoll = 1;
        nearest = new FindNearestData(SPACE_2, 1, Arrays.asList(SPACE_1, SPACE_2));
        fireReckoning();
        assertTrue(effects.contains("changeActiveInvestigator:" + target));
        assertTrue(effects.contains("gainCondition:CURSED"));

        effects.clear();
        nearest = null;
        fireReckoning();
        assertTrue(effects.contains("rollDie"));
        assertFalse(effects.contains("gainCondition:CURSED"));
    }

    private void fireReckoning() {
        events.fireDirectEvent(DirectEvent.RECKONING_MONSTER, ReckoningFireType.CHARGE);
        events.fireDirectEvent(DirectEvent.RECKONING_MONSTER, ReckoningFireType.FIRE);
        drain();
    }

    private void drain() {
        while (!pending.isEmpty()) {
            pending.remove(0).run();
        }
    }

    private <M extends AbstractMonsterInfo> M monster(M monster, LocationId location) {
        monster.setCurrentLocation(location);
        return monster;
    }

    private void investigator(InvestigatorId id, LocationId location) {
        InvestigatorInfo info = mock(InvestigatorInfo.class, (name, a) -> {
            if (name.equals("getInvestigatorId")) return id;
            throw new AssertionError(name);
        });
        onBoard.add(mock(InvestigatorRead.class, (name, a) -> {
            if (name.equals("getCurrentLocationId")) return location;
            if (name.equals("getInfo")) return info;
            throw new AssertionError(name);
        }));
    }

    private static <T> T mock(Class<T> type, BiFunction<String, Object[], Object> handler) {
        return type.cast(Proxy.newProxyInstance(type.getClassLoader(), new Class[]{type},
                (proxy, method, args) -> {
                    if (method.getDeclaringClass() == Object.class) {
                        if (method.getName().equals("equals")) return proxy == args[0];
                        if (method.getName().equals("hashCode")) return System.identityHashCode(proxy);
                        return type.getSimpleName();
                    }
                    return handler.apply(method.getName(), args);
                }));
    }
}
