package sk.sivak.eldritchhorror.core.eventlistener.ancientone.yig;

import java.lang.reflect.Proxy;
import java.util.*;
import java.util.function.BiFunction;
import org.junit.*;
import java8.features.function.Consumer;
import sk.sivak.eldritchhorror.core.constants.*;
import sk.sivak.eldritchhorror.core.constants.ancientone.*;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.clue.ClueInfo;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.constants.monster.*;
import sk.sivak.eldritchhorror.core.constants.monster.epic.*;
import sk.sivak.eldritchhorror.core.constants.reference.ReferenceInfo;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.monster.*;
import sk.sivak.eldritchhorror.core.eventqueue.EventQueueImpl;
import sk.sivak.eldritchhorror.core.eventtype.*;
import sk.sivak.eldritchhorror.core.eventtype.data.*;
import sk.sivak.eldritchhorror.core.eventtype.data.combat.CombatData;
import sk.sivak.eldritchhorror.core.eventtype.data.monster.DefeatMonsterData;
import sk.sivak.eldritchhorror.core.eventtype.data.test.TestData;
import sk.sivak.eldritchhorror.core.model.*;
import sk.sivak.eldritchhorror.core.model.util.*;
import sk.sivak.eldritchhorror.core.service.*;
import static org.junit.Assert.*;

/** Uses real event dispatch and deferred command insertion, with UI/service effects recorded. */
public class YigRulesTest {
    private EventQueueImpl events;
    private AncientOne ancient;
    private List<String> effects;
    private List<Runnable> pending, inserted;
    private List<MonsterInfo> monsters;
    private ServicePlatform platform;
    @Before public void setup() {
        ServicePlatform.nullifyInstance();platform=ServicePlatform.get();events=new EventQueueImpl();
        platform.setEventQueue(events);ancient=new AncientOne();ancient.setAncientOneInfo(AncientOneHelper.createYig());
        effects=new ArrayList<>();pending=new ArrayList<>();monsters=new ArrayList<>();
        platform.setModel(mock(ModelRead.class,(name,a) -> {
            if(name.equals("getAncientOne"))return ancient;
            if(name.equals("getReferenceCard"))return mock(ReferenceInfo.class,(n,b) -> 2);
            throw new AssertionError(name);
        }));
        platform.setService(mock(Service.class,(name,a) -> {
            if(name.equals("addEventCommand")){queue(() -> ((Consumer<Object>)a[0]).accept(null));return null;}
            if(name.equals("hold") || name.equals("release") || name.startsWith("convert"))return null;
            queue(() -> effects.add(name));return null;
        }));
        platform.setGameService(mock(GameService.class,(name,a) -> {
            if(name.equals("spawnMonster"))queue(() -> {
                SpawnMonsterData data=(SpawnMonsterData)a[0];
                CultistMonster cultist=new CultistMonster();cultist.setMonsterId(NonEpicMonsterId.CULTIST);
                cultist.setCurrentLocation(data.getLocationId());monsters.add(cultist);effects.add("spawn");
            });
            else queue(() -> effects.add(name+(name.equals("gainCondition")?":"+a[0]:"")));
            return null;
        }));
        platform.setDoomOmenService(mock(DoomOmenService.class,(name,a) -> {
            queue(() -> {
                effects.add(name);
                if(name.equals("increaseAncientOnePower"))ancient.increasePower((Integer)a[0]);
            });return null;
        }));
        platform.setMonsterService(mock(MonsterService.class,(name,a) -> {queue(() -> effects.add(name));return null;}));
        platform.setMonsterCup(mock(MonsterCupRead.class,(name,a) -> {
            if(name.equals("getMonsters"))return monsters;
            if(name.equals("getMonstersAtLocation")) {
                List<MonsterInfo> result=new ArrayList<>();for(MonsterInfo m:monsters)if(m.getCurrentLocation()==a[0])result.add(m);return result;
            }
            throw new AssertionError(name);
        }));
        platform.setExpeditionDeck(mock(ExpeditionDeckRead.class,(n,a) -> LocationId.THE_AMAZON));
    }
    @After public void tearDown(){ServicePlatform.nullifyInstance();}
    private <T>T mock(Class<T> type,BiFunction<String,Object[],Object> handler) {
        return type.cast(Proxy.newProxyInstance(type.getClassLoader(),new Class[]{type},(p,m,a) -> handler.apply(m.getName(),a)));
    }
    private void queue(Runnable action){(inserted==null?pending:inserted).add(action);}
    private void drain() {
        int limit=1000;
        while(!pending.isEmpty()) {
            if(--limit==0)throw new AssertionError("Command loop");
            Runnable action=pending.remove(0);inserted=new ArrayList<>();action.run();
            pending.addAll(0,inserted);inserted=null;
        }
    }
    private CombatData combat(MonsterInfo monster,int lostHealth,int lostSanity,int score) {
        CombatData data=new CombatData();data.setMonsterInfo(monster);data.setHealthLost(lostHealth);data.setSanityLost(lostSanity);
        TestData test=new TestData();test.setDiceRolls(Collections.emptyList());test.setScoreBonus(score);data.setHorrorTestResult(test);return data;
    }
    @Test public void cultistsPoisonOnlyWhenHealthWasActuallyLostAndUnregisterCleanly() {
        CultistMonster monster=new CultistMonster();YigCultistMonsterListener listener=new YigCultistMonsterListener();listener.register(monster);
        events.fireBeforeEvent(BeforeAfterEvent.DESTROY_MONSTER_HEALTH,combat(monster,0,0,0));drain();assertTrue(effects.isEmpty());
        events.fireBeforeEvent(BeforeAfterEvent.DESTROY_MONSTER_HEALTH,combat(monster,1,0,0));drain();assertEquals(Collections.singletonList("gainCondition:POISONED"),effects);
        listener.unregister();events.fireBeforeEvent(BeforeAfterEvent.DESTROY_MONSTER_HEALTH,combat(monster,1,0,0));drain();assertEquals(1,effects.size());
    }
    @Test public void loadingAwakenedCultistKeepsItsWounds() {
        ancient.awaken();CultistMonster monster=new CultistMonster();monster.setCurrentHealth(1);monsters.add(monster);
        new YigCultistMonsterListener().justRegisterListeners(monster);
        assertEquals(2,(int)monster.getToughness());assertEquals(1,(int)monster.getCurrentHealth());
        assertEquals(3,(int)monster.getDamage());assertEquals(2,(int)monster.getHorror());
    }
    @Test public void ambushingAwakenedCultistStartsAtFullToughness() {
        ancient.awaken();CultistMonster monster=new CultistMonster();
        new YigCultistMonsterListener().justRegisterListeners(monster);
        assertEquals(2,(int)monster.getCurrentHealth());
    }
    @Test public void childrenEscapeOnFailedObservationAndSkipStrength() {
        ChildrenOfYigMonster monster=new ChildrenOfYigMonster();new YigEpicMonsterListener().register(monster);
        assertEquals(4,(int)monster.getToughness());
        events.fireAfterEvent(BeforeAfterEvent.AFTER_HORROR_CHECK,combat(monster,0,0,1));drain();assertTrue(effects.isEmpty());
        events.fireAfterEvent(BeforeAfterEvent.AFTER_HORROR_CHECK,combat(monster,0,0,0));drain();
        assertEquals(Arrays.asList("highlightSpecialText","skipBeforeEvent"),effects);
        // The escape is shown on the map only after the combat screen is gone.
        effects.clear();
        events.fireAfterEvent(BeforeAfterEvent.HIDE_COMBAT_TABLE,null);drain();
        assertEquals(Arrays.asList("moveMonster"),effects);
        effects.clear();
        events.fireAfterEvent(BeforeAfterEvent.HIDE_COMBAT_TABLE,null);drain();assertTrue(effects.isEmpty());
    }
    @Test public void wingedSerpentRespectsPreventionAndYigCursesSanityLoss() {
        WingedSerpentMonster wing=new WingedSerpentMonster();new YigEpicMonsterListener().register(wing);
        events.fireBeforeEvent(BeforeAfterEvent.DESTROY_MONSTER_HEALTH,combat(wing,0,0,0));drain();assertTrue(effects.isEmpty());
        events.fireBeforeEvent(BeforeAfterEvent.DESTROY_MONSTER_HEALTH,combat(wing,1,0,0));drain();assertEquals("gainCondition:POISONED",effects.get(0));
        YigMonster yig=new YigMonster();new YigEpicMonsterListener().register(yig);assertEquals(5,(int)yig.getToughness());
        events.fireAfterEvent(BeforeAfterEvent.AFTER_HORROR_CHECK,combat(yig,0,0,1));drain();assertEquals(1,effects.size());
        events.fireAfterEvent(BeforeAfterEvent.AFTER_HORROR_CHECK,combat(yig,0,1,0));drain();assertEquals("gainCondition:CURSED",effects.get(1));
    }
    @Test public void loadedEpicMonsterDoesNotHeal() {
        YigMonster yig=new YigMonster();yig.setCurrentHealth(2);new YigEpicMonsterListener().justRegisterListeners(yig);
        assertEquals(5,(int)yig.getToughness());assertEquals(2,(int)yig.getCurrentHealth());
    }
    @Test public void reckoningCountsMonstersAfterTheNewCultistSpawns() {
        new YigInitListener().load();events.fireDirectEvent(DirectEvent.RECKONING_ANCIENT_ONE,ReckoningFireType.CHARGE);drain();assertTrue(effects.isEmpty());
        events.fireDirectEvent(DirectEvent.RECKONING_ANCIENT_ONE,ReckoningFireType.FIRE);drain();assertFalse(effects.contains("advanceDoom"));
        events.fireDirectEvent(DirectEvent.RECKONING_ANCIENT_ONE,ReckoningFireType.FIRE);drain();assertEquals("advanceDoom",effects.get(effects.size()-1));
    }
    @Test public void lastEldritchTokenLosesBeforeTheDoomActionIsSkipped() {
        ancient.awaken();ancient.increasePower(1);new YigInitListener().load();
        events.fireBeforeEvent(BeforeAfterEvent.ADVANCE_DOOM,3);drain();
        assertEquals(0,ancient.getAncientOneInfo().getPower());
        assertEquals(Arrays.asList("increaseAncientOnePower","displayText","restartGame","skipAfterEvent"),effects);
    }
    @Test public void awakeningDefersUnregistrationUntilEventDispatchCompletes() {
        platform.setDoomTrack(mock(DoomTrackRead.class,(n,a) -> 0));new YigInitListener().load();
        events.fireAfterEvent(BeforeAfterEvent.ADVANCE_DOOM,null);drain();assertEquals(Collections.singletonList("ancientOneAwakens"),effects);
        events.fireAfterEvent(BeforeAfterEvent.ADVANCE_DOOM,null);drain();assertEquals(1,effects.size());
    }
    @Test public void loadRestoresMysteryListenersWithoutRepeatingSetupAndFinalBattleWinsImmediately() {
        MysteryCardInfo finale=YigMysteryDeck.create(2).get(3);new YigMysteryListener(finale).justRegisterListeners(0);drain();assertTrue(effects.isEmpty());
        events.fireAfterEvent(BeforeAfterEvent.DEFEAT_MONSTER,new DefeatMonsterData(new YigMonster(),true));drain();
        assertEquals((int)finale.getMysteryComplexity(),(int)finale.getProgress());
        assertEquals(Collections.singletonList("resolveCurrentMystery"),effects);
    }
    @Test public void advancingCrownBanksCluesWithoutBypassingItsTestAndSacrifice() {
        MysteryCardInfo crown=YigMysteryDeck.create(2).stream()
                .filter(c -> c.getMysteryCardId()==MysteryCardId.Yig.CROWN_OF_THE_SERPENT).findFirst().get();
        YigMysteryListener listener=new YigMysteryListener(crown);
        listener.advanceActiveMystery();drain();listener.advanceActiveMystery();drain();
        assertEquals(2,crown.getClueCredit());assertEquals(0,(int)crown.getProgress());
        assertFalse(effects.contains("advanceCurrentMysteryCard"));
    }
    @Test public void advancingRiseAddsPoolTokenWithoutRemovingBoardTokens() {
        MysteryCardInfo rise=YigMysteryDeck.create(4).stream()
                .filter(c -> c.getMysteryCardId()==MysteryCardId.Yig.RISE_OF_THE_SERPENT_PEOPLE).findFirst().get();
        List<LocationId> before=new ArrayList<>(rise.getPinLocations());
        new YigMysteryListener(rise).advanceActiveMystery();drain();
        assertEquals(1,(int)rise.getProgress());assertEquals(before,rise.getPinLocations());
    }
    @Test public void migrationAutomaticallyMovesCluesToNearestExpeditionsIncludingTies() {
        LocationMap map=new LocationMap();map.initLocations();platform.setLocationMap(map);
        List<ClueInfo> clues=new ArrayList<>();
        LocationId[] locations=LocationId.values();
        for(int i=0;i<locations.length;i++) {
            LocationId current=locations[i], spawn=locations[(i+1)%locations.length];
            clues.add(mock(ClueInfo.class,(name,a) -> name.equals("getSpawnLocationId")?spawn:current));
        }
        platform.setCluePoolRead(mock(CluePoolRead.class,(name,a) -> clues));
        Map<LocationId,LocationId> moves=new EnumMap<>(LocationId.class);
        platform.setTokenService(mock(TokenService.class,(name,a) -> {
            assertEquals("moveClue",name);
            queue(() -> assertNull("Each Clue moves only once",moves.put((LocationId)a[0],(LocationId)a[1])));
            return null;
        }));
        MysteryCardInfo migration=YigMysteryDeck.create(2).stream()
                .filter(c -> c.getMysteryCardId()==MysteryCardId.Yig.MIGRATION_OF_SERPENTS).findFirst().get();
        new YigMysteryListener(migration).register();drain();
        assertEquals(clues.size(),moves.size());
        assertEquals(Collections.singletonList("showCurrentMysteryCard"),effects);
        boolean coveredTie=false;
        for(ClueInfo clue:clues) {
            List<LocationId> nearest=YigEffects.nearest(clue.getCurrentLocationId(),YigEffects.EXPEDITIONS);
            coveredTie|=nearest.size()>1;
            assertEquals(Collections.min(nearest),moves.get(clue.getSpawnLocationId()));
        }
        assertTrue("Exercise tied destinations as well as single destinations",coveredTie);
    }
    @Test public void mysteryPinsOnlyMarkSpecialEncountersOnSetupAndLoad() {
        platform.setCluePoolRead(mock(CluePoolRead.class,(name,a) ->
                name.equals("getClueLocations")?Collections.emptySet():Collections.emptyList()));
        for(MysteryCardInfo card:YigMysteryDeck.create(2)) {
            effects.clear();monsters.clear();
            MysteryCardId id=card.getMysteryCardId();
            boolean encounter=id==MysteryCardId.Yig.KN_YAN_UNEARTHED
                    || id==MysteryCardId.Yig.RISE_OF_THE_SERPENT_PEOPLE;
            boolean epic=id==MysteryCardId.Yig.DESCENDANTS_OF_YIG
                    || id==MysteryCardId.Yig.THE_WINGED_SERPENT || id==MysteryCardId.Yig.SERPENTS_NEST;
            List<LocationId> locations=card.getPinLocations()==null?Collections.emptyList():new ArrayList<>(card.getPinLocations());
            YigMysteryListener listener=new YigMysteryListener(card);
            listener.register();drain();
            assertTrue(id.toString(),effects.contains("showCurrentMysteryCard"));
            assertEquals(id.toString(),encounter,effects.contains("spawnRedPins"));
            assertEquals(id.toString(),epic,effects.contains("spawn"));
            if(epic) assertEquals(locations.get(0),monsters.get(0).getCurrentLocation());

            listener.unregister();drain();
            assertEquals(id.toString(),encounter,effects.contains("clearRedPins"));
            effects.clear();
            listener.justRegisterListeners(0);
            listener.justAddRedPins();drain();
            assertEquals(id.toString(),encounter,effects.contains("justAddRedPins"));
            assertFalse(id.toString(),effects.contains("spawn"));
            listener.unregister();drain();
        }
    }
}
