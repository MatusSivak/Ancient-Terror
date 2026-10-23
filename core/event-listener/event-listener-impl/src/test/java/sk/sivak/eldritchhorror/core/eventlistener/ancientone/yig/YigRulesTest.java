package sk.sivak.eldritchhorror.core.eventlistener.ancientone.yig;

import java.lang.reflect.Proxy;
import java.util.*;
import java.util.function.BiFunction;
import rx.Single;
import sk.sivak.eldritchhorror.core.constants.question.*;
import sk.sivak.eldritchhorror.core.constants.investigator.*;
import org.junit.*;
import java8.features.function.Consumer;
import sk.sivak.eldritchhorror.core.constants.*;
import sk.sivak.eldritchhorror.core.constants.ancientone.*;
import sk.sivak.eldritchhorror.core.constants.artifact.ArtifactId;
import sk.sivak.eldritchhorror.core.constants.artifact.ArtifactInfo;
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
import sk.sivak.eldritchhorror.core.eventtype.data.card.GainCardData;
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
            else if(name.equals("ask")) {
                Question<?> q=(Question<?>)a[0];
                queue(() -> effects.add("notice:"+q.getTitle()));
                return Single.just(new Answer<Object,Object>());
            }
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

    @Test public void childrenAlwaysEscapeToADifferentSpace() {
        ChildrenOfYigMonster monster=new ChildrenOfYigMonster();
        new YigEpicMonsterListener().register(monster);
        int[] moves={0};
        platform.setMonsterService(mock(MonsterService.class,(name,a) -> {
            if(name.equals("moveMonster")) {
                assertSame(monster,a[0]);assertNotEquals(monster.getCurrentLocation(),a[1]);
                monster.setCurrentLocation((LocationId)a[1]);moves[0]++;
            }
            return null;
        }));
        for(LocationId origin:LocationId.values()) {
            monster.setCurrentLocation(origin);
            for(int i=0;i<10;i++) {
                events.fireAfterEvent(BeforeAfterEvent.AFTER_HORROR_CHECK,combat(monster,0,0,0));drain();
                events.fireAfterEvent(BeforeAfterEvent.HIDE_COMBAT_TABLE,null);drain();
            }
        }
        assertEquals(LocationId.values().length*10,moves[0]);
    }

    @Test public void replacingSleepingYigListenersDoesNotDoubleAwakenedReckoning() {
        YigInitListener sleeping=new YigInitListener();sleeping.load();
        sleeping.unregister();ancient.awaken();new YigInitListener().load();
        events.fireDirectEvent(DirectEvent.RECKONING_ANCIENT_ONE,ReckoningFireType.FIRE);drain();
        assertEquals(1,Collections.frequency(effects,"spawn"));
        assertEquals(1,Collections.frequency(effects,"onTriggeredReckoning"));
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
        assertEquals(Arrays.asList("onTriggeredReckoning","notice:ancientOne.yig.reckoning.cultist","spawn"),effects);
        effects.clear();
        events.fireDirectEvent(DirectEvent.RECKONING_ANCIENT_ONE,ReckoningFireType.FIRE);drain();
        // Each automatic change is explained before it happens.
        assertEquals(Arrays.asList("onTriggeredReckoning","notice:ancientOne.yig.reckoning.cultist","spawn",
                "notice:ancientOne.yig.reckoning.doom","advanceDoom"),effects);
    }
    @Test public void awakenedDoomOnlyLosesAtMidnightWithoutUsingTokens() {
        ancient.awaken();new YigInitListener().load();
        platform.setTutorialService(mock(TutorialService.class,(name,a) -> {
            assertEquals("displayChalkboard", name);
            assertEquals("ancientOne.yig.endGame", a[0]);
            assertEquals(true, a[3]);
            queue(() -> effects.add("endingDialog"));
            return null;
        }));
        platform.setDoomTrack(mock(DoomTrackRead.class,(n,a) -> -1));
        events.fireBeforeEvent(BeforeAfterEvent.ADVANCE_DOOM,1);drain();assertTrue(effects.isEmpty());
        events.fireAfterEvent(BeforeAfterEvent.ADVANCE_DOOM,null);drain();assertTrue(effects.isEmpty());
        platform.setDoomTrack(mock(DoomTrackRead.class,(n,a) -> 0));
        events.fireAfterEvent(BeforeAfterEvent.ADVANCE_DOOM,null);drain();
        assertEquals(Arrays.asList("endingDialog","restartGame"),effects);
    }
    @Test public void awakenedReckoningSpawnsExactlyOneRandomCultistWithoutAPicker() {
        ancient.awaken();new YigInitListener().load();
        events.fireDirectEvent(DirectEvent.RECKONING_ANCIENT_ONE,ReckoningFireType.CHARGE);drain();assertTrue(monsters.isEmpty());
        events.fireDirectEvent(DirectEvent.RECKONING_ANCIENT_ONE,ReckoningFireType.FIRE);drain();
        assertEquals(1,monsters.size());assertNotNull(monsters.get(0).getCurrentLocation());
        assertEquals(Arrays.asList("onTriggeredReckoning","notice:ancientOne.yig.reckoning.cultistRandom","spawn"),effects);
    }
    @Test public void retreatChoiceCanKeepDoomRetreatOrReplaceItWithCurseRemoval() {
        for (boolean discard : new boolean[]{false,true}) {
            setup();ancient.awaken();new YigInitListener().load();
            InvestigatorInfo info=mock(InvestigatorInfo.class,(n,a)->InvestigatorId.THE_SAILOR);
            InvestigatorRead investigator=mock(InvestigatorRead.class,(n,a)->info);
            platform.setInvestigators(mock(InvestigatorsRead.class,(n,a)->Collections.singletonList(investigator)));
            platform.setConditionsDeck(mock(ConditionsDeckRead.class,(n,a)->n.equals("hasCondition")?true:null));
            platform.setGameService(mock(GameService.class,(n,a)->{
                assertEquals("ask",n);Question<Boolean> q=(Question<Boolean>)a[0];
                assertEquals(2,q.getOptions().size());assertEquals("Retreat Doom",q.getOptions().get(0).getName());
                assertEquals("Discard Cursed",q.getOptions().get(1).getName());
                Answer<Boolean,Object> answer=new Answer<>();answer.setResponseData(discard);return Single.just(answer);
            }));
            events.fireBeforeEvent(BeforeAfterEvent.RETREAT_DOOM,null);drain();
            assertEquals(discard?Arrays.asList("discardConditionFromInvestigator","skipAfterEvent"):Collections.emptyList(),effects);
        }
    }
    @Test public void noCurseAllowsDoomRetreatWithoutAnEmptyChoice() {
        ancient.awaken();new YigInitListener().load();
        platform.setInvestigators(mock(InvestigatorsRead.class,(n,a)->Collections.emptyList()));
        events.fireBeforeEvent(BeforeAfterEvent.RETREAT_DOOM,null);drain();assertTrue(effects.isEmpty());
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

    @Test public void crownResolutionWithoutActiveInvestigatorOrCrownOwnerDoesNothing() {
        resolveCrown(null, false, false);
    }

    @Test public void crownArtifactChoiceWorksOnNewAndLoadedMysteryAndRespectsOwnership() {
        for (boolean loaded : new boolean[]{false, true}) {
            for (boolean accept : new boolean[]{false, true}) {
                setup();
                ArtifactsDeck deck = new ArtifactsDeck();
                deck.setUnlockedArtifactsSupplier(() -> Collections.singletonList(ArtifactId.SERPENT_CROWN));
                deck.createDeck();
                platform.setArtifactsDeck(deck);
                GainCardData original = new GainCardData(null, true);
                GainCardData[] selected = {original};
                platform.setService(mock(Service.class, (n,a) -> {
                    if (n.equals("convertTo")) {
                        assertEquals(GainCardData.class, a[0]);
                        queue(() -> selected[0] = ((java8.features.function.Supplier<GainCardData>) a[1]).get());
                    } else assertTrue(n.equals("hold") || n.equals("release"));
                    return null;
                }));
                platform.setGameService(mock(GameService.class, (n,a) -> {
                    if (n.equals("showCurrentMysteryCard")) return null;
                    assertEquals("ask", n);
                    assertEquals("Gain the Serpent Crown Artifact instead?", ((Question<?>) a[0]).getTitle());
                    return Single.create(sub -> queue(() -> {
                        effects.add("ask");
                        Answer<Boolean,Object> answer = new Answer<>(); answer.setResponseData(accept);
                        sub.onSuccess(answer);
                    }));
                }));
                MysteryCardInfo crown = YigMysteryDeck.create(2).stream()
                        .filter(c -> c.getMysteryCardId() == MysteryCardId.Yig.CROWN_OF_THE_SERPENT).findFirst().get();
                YigMysteryListener listener = new YigMysteryListener(crown);
                if (loaded) listener.justRegisterListeners(0); else listener.register();
                events.fireBeforeEvent(BeforeAfterEvent.SELECT_CARD_TO_GAIN, original); drain();
                assertEquals(Collections.singletonList("ask"), effects);
                if (accept) assertEquals(ArtifactId.SERPENT_CROWN, selected[0].getArtifactId());
                else assertSame(original, selected[0]);

                ArtifactInfo artifact = deck.removeFromDeck(ArtifactId.SERPENT_CROWN);
                deck.addToInvestigator(InvestigatorId.THE_SAILOR, artifact);
                effects.clear();
                events.fireBeforeEvent(BeforeAfterEvent.SELECT_CARD_TO_GAIN, original); drain();
                assertTrue("An owned Crown must not be offered again", effects.isEmpty());
                deck.discard(InvestigatorId.THE_SAILOR, artifact);
                events.fireBeforeEvent(BeforeAfterEvent.SELECT_CARD_TO_GAIN, original); drain();
                assertEquals(Collections.singletonList("ask"), effects);
                listener.unregister();
                effects.clear();
                events.fireBeforeEvent(BeforeAfterEvent.SELECT_CARD_TO_GAIN, original); drain();
                assertTrue("The offer ends with the mystery", effects.isEmpty());
            }
        }
    }

    @Test public void crownChoiceAndFailedTestRestoreNoActiveInvestigator() {
        for (boolean attempt : new boolean[]{false, true}) {
            setup();
            resolveCrown(null, true, attempt);
        }
    }

    @Test public void crownChoiceAndFailedTestRestorePreviousInvestigator() {
        for (boolean attempt : new boolean[]{false, true}) {
            setup();
            resolveCrown(InvestigatorId.THE_ASTRONOMER, true, attempt);
        }
    }

    private void resolveCrown(InvestigatorId previous, boolean hasCrown, boolean attempt) {
        InvestigatorId owner = InvestigatorId.THE_SAILOR;
        InvestigatorId[] active = {previous};
        InvestigatorRead holder = mock(InvestigatorRead.class, (n,a) ->
                mock(InvestigatorInfo.class, (nn,aa) -> owner));
        platform.setInvestigators(mock(InvestigatorsRead.class, (n,a) -> {
            if (n.equals("getActiveInvestigatorId")) return active[0];
            if (n.equals("getActiveInvestigator")) return active[0] == null ? null
                    : mock(InvestigatorRead.class, (nn,aa) -> mock(InvestigatorInfo.class, (nnn,aaa) -> active[0]));
            if (n.equals("getOnBoardInvestigators")) return Collections.singletonList(holder);
            throw new AssertionError(n);
        }));
        platform.setArtifactsDeck(mock(ArtifactsDeckRead.class, (n,a) -> {
            assertEquals("hasArtifact", n);
            assertEquals(owner, a[0]);
            assertEquals(sk.sivak.eldritchhorror.core.constants.artifact.ArtifactId.SERPENT_CROWN, a[1]);
            return hasCrown;
        }));
        platform.setInvestigatorService(mock(InvestigatorService.class, (n,a) -> {
            if (n.equals("showActiveInvestigator")) {
                assertEquals(false, a[0]);
                queue(() -> effects.add("show:" + active[0]));
                return null;
            }
            assertTrue(n.equals("changeActiveInvestigator") || n.equals("unsetActiveInvestigator"));
            if (n.equals("changeActiveInvestigator")) assertNotNull("Use unset for an absent investigator", a[0]);
            queue(() -> {
                active[0] = n.equals("unsetActiveInvestigator") ? null : (InvestigatorId) a[0];
                effects.add("active:" + active[0]);
            });
            return null;
        }));
        platform.setGameService(mock(GameService.class, (n,a) -> {
            if (n.equals("showCurrentMysteryCard")) {
                assertEquals(false, a[0]);
                assertEquals(false, a[1]);
                queue(() -> effects.add("mystery"));
                return null;
            }
            assertEquals("ask", n);
            return Single.create(sub -> queue(() -> {
                assertEquals("The Crown holder answers the question", owner, active[0]);
                effects.add("ask");
                Answer<Boolean,Object> answer = new Answer<>(); answer.setResponseData(attempt);
                sub.onSuccess(answer);
            }));
        }));
        platform.setTestService(mock(TestService.class, (n,a) -> {
            assertEquals("test", n); assertEquals(Stat.WILL, a[0]); assertEquals(-1, a[1]);
            return Single.create(sub -> queue(() -> {
                assertEquals("Keep the Crown holder active until the test resolves", owner, active[0]);
                effects.add("test");
                TestData result = new TestData(); result.setDiceRolls(Collections.emptyList());
                sub.onSuccess(result);
            }));
        }));
        MysteryCardInfo crown = YigMysteryDeck.create(2).stream()
                .filter(c -> c.getMysteryCardId() == MysteryCardId.Yig.CROWN_OF_THE_SERPENT).findFirst().get();
        YigMysteryListener listener = new YigMysteryListener(crown);
        listener.justRegisterListeners(0);
        events.fireBeforeEvent(BeforeAfterEvent.RESOLVE_CURRENT_MYSTERY, null);
        drain();
        assertEquals(previous, active[0]);
        assertEquals(0, (int) crown.getProgress());
        if (!hasCrown) assertTrue(effects.isEmpty());
        else {
            List<String> expected = new ArrayList<>(Arrays.asList("active:" + owner, "mystery", "show:" + owner, "ask"));
            if (attempt) expected.add("test");
            expected.add("active:" + previous);
            if (previous != null) expected.add("show:" + previous);
            assertEquals(expected, effects);
        }
        listener.unregister();
    }
    @Test public void advancingRiseAddsPoolTokenWithoutRemovingBoardTokens() {
        MysteryCardInfo rise=YigMysteryDeck.create(4).stream()
                .filter(c -> c.getMysteryCardId()==MysteryCardId.Yig.RISE_OF_THE_SERPENT_PEOPLE).findFirst().get();
        List<LocationId> before=new ArrayList<>(rise.getPinLocations());
        new YigMysteryListener(rise).advanceActiveMystery();drain();
        assertEquals(1,(int)rise.getProgress());assertEquals(before,rise.getPinLocations());
    }
    @Test public void removingRiseTokenClearsOnlyThatPin() throws Exception {
        MysteryCardInfo rise=YigMysteryDeck.create(4).stream()
                .filter(c -> c.getMysteryCardId()==MysteryCardId.Yig.RISE_OF_THE_SERPENT_PEOPLE).findFirst().get();
        List<LocationId> pins=rise.getPinLocations();
        assertEquals(2,pins.size());
        LocationId removed=pins.get(0), remaining=pins.get(1);
        List<String> pinEffects=new ArrayList<>();
        platform.setGameService(mock(GameService.class,(name,a) -> {
            queue(() -> pinEffects.add(name+(a==null?"":":"+Arrays.toString(a))));return null;
        }));
        java.lang.reflect.Method removeToken=YigMysteryListener.class.getDeclaredMethod("removeToken",LocationId.class);
        removeToken.setAccessible(true);
        removeToken.invoke(new YigMysteryListener(rise),removed);drain();
        assertEquals(Collections.singletonList(remaining),rise.getPinLocations());
        assertEquals(Arrays.asList("advanceCurrentMysteryCard:[1]","clearRedPin:["+removed+"]"),pinEffects);
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
