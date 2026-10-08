package sk.sivak.eldritchhorror.core.eventlistener.ancientone.yig;

import java.lang.reflect.Proxy;
import java.util.*;
import java.util.function.BiFunction;
import java8.features.function.Consumer;
import org.junit.*;
import rx.Single;
import rx.SingleSubscriber;
import sk.sivak.eldritchhorror.core.constants.MysteryCardInfo;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.investigator.*;
import sk.sivak.eldritchhorror.core.constants.location.*;
import sk.sivak.eldritchhorror.core.constants.monster.CultistMonster;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.research.yig.YigResearchEncounter;
import sk.sivak.eldritchhorror.core.eventqueue.EventQueueImpl;
import sk.sivak.eldritchhorror.core.eventtype.data.combat.CombatData;
import sk.sivak.eldritchhorror.core.eventtype.data.test.TestData;
import sk.sivak.eldritchhorror.core.model.*;
import sk.sivak.eldritchhorror.core.service.*;
import static org.junit.Assert.*;

/** Pauses the command queue at buttons, tests, and combat, as the real UI does. */
public class YigEncounterPresentationTest {
    private final List<Runnable> pending = new ArrayList<>();
    private List<Runnable> inserted;
    private final List<String> infos = new ArrayList<>(), effects = new ArrayList<>();
    private final List<String> paperTransitions = new ArrayList<>();
    private SingleSubscriber<? super Integer> button;
    private SingleSubscriber<? super TestData> test;
    private SingleSubscriber<? super CombatData> combat;
    private String[] buttons;
    private boolean waiting;
    private int papers;
    private boolean paperVisible;
    private ServicePlatform platform;

    @Before public void setup() {
        ServicePlatform.nullifyInstance();
        platform = ServicePlatform.get();
        platform.setEventQueue(new EventQueueImpl());
        platform.setService(mock(Service.class, (name, args) -> {
            if (name.equals("addEventCommand")) queue(() -> ((Consumer<Object>) args[0]).accept(null));
            if (name.equals("showKnyanBackground") || name.equals("hideBackground")) queue(() -> effects.add(name));
            return null;
        }));
        platform.setEncounterService(mock(EncounterService.class, (name, args) -> {
            if (name.equals("showTypewriterPaper")) queue(() -> {
                assertFalse("Do not replay the entrance animation on visible paper", paperVisible);
                if ((Boolean) args[0]) papers++;
                assertEquals("Encounter paper must remain available", 1, papers);
                paperVisible = true;
                paperTransitions.add("show");
            });
            if (name.equals("finishTypewriterPaper")) queue(() -> {
                assertEquals("Finish each encounter paper exactly once", 1, papers);
                papers--;
                paperVisible = false;
            });
            if (name.equals("hideTypewriterPaper")) queue(() -> {
                assertTrue("Do not hide an already hidden paper", paperVisible);
                paperVisible = false;
                paperTransitions.add("hide");
            });
            if (name.equals("typeInfo")) queue(() -> infos.add((String) args[0]));
            if (name.equals("typeFlavor")) queue(() -> effects.add("flavor"));
            if (name.equals("displayButtons")) return Single.<Integer>create(sub -> queue(() -> {
                assertTrue("Buttons must be on a visible typewriter paper", paperVisible);
                buttons = (String[]) args[0]; button = sub; waiting = true;
            }));
            return null;
        }));
        platform.setTestService(mock(TestService.class, (name, args) -> {
            if (name.equals("test")) return Single.<TestData>create(sub -> queue(() -> {
                assertFalse("Hide the paper while rolling dice", paperVisible);
                effects.add("test:" + args[0] + ":" + args[1]); test = sub; waiting = true;
            }));
            throw new AssertionError(name);
        }));
        platform.setMonsterService(mock(MonsterService.class, (name, args) ->
                Single.<CombatData>create(sub -> queue(() -> { combat = sub; waiting = true; }))));
        platform.setTokenService(mock(TokenService.class, (name, args) -> {
            queue(() -> effects.add(name)); return null;
        }));
        platform.setGameService(mock(GameService.class, (name, args) -> {
            queue(() -> effects.add(name + ":" + args[0])); return null;
        }));
        platform.setDoomOmenService(mock(DoomOmenService.class, (name, args) -> {
            queue(() -> effects.add(name)); return null;
        }));
        InvestigatorInfo info = mock(InvestigatorInfo.class, (name, args) -> InvestigatorId.THE_SAILOR);
        InvestigatorRead investigator = mock(InvestigatorRead.class, (name, args) -> {
            if (name.equals("getInfo")) return info;
            if (name.equals("getCurrentLocationId")) return LocationId.THE_AMAZON;
            return 0;
        });
        platform.setInvestigators(mock(InvestigatorsRead.class, (name, args) -> investigator));
        platform.setConditionsDeck(mock(ConditionsDeckRead.class, (name, args) -> false));
    }
    @After public void cleanup() { ServicePlatform.nullifyInstance(); }

    @Test public void failedGuardianTestReturnsPaperOnceAndWaitsForAmbushConfirmation() {
        startSpecial(6); click(); finishTest(false);
        assertEquals(Arrays.asList("show", "hide", "show"), paperTransitions);
        assertTrue(infos.contains("[#BAD]A Serpent People ambushes you.[]"));
        assertNull("Ambush waits for OK", combat);
        click();
        assertEquals(Arrays.asList("show", "hide", "show", "hide"), paperTransitions);
        assertNotNull(combat);
    }

    @Test public void defeatedGuardianExplainsThatAmbushDoesNotAdvanceMystery() {
        guardianOutcome(0, "You defeated the Serpent People.");
    }

    @Test public void survivingGuardianStillExplainsEncounterCompletion() {
        guardianOutcome(2, "The ambush is over.");
    }

    private void guardianOutcome(int health, String message) {
        startSpecial(6); click(); finishTest(false); click();
        CultistMonster monster = new CultistMonster(); monster.setCurrentHealth(health);
        CombatData result = new CombatData(); result.setMonsterInfo(monster);
        resume(() -> combat.onSuccess(result));
        assertTrue(paperVisible);
        assertTrue(allInfo().contains(message));
        assertTrue(allInfo().contains("without advancing the Active Mystery"));
        assertEquals(1, papers);
        assertFalse(effects.contains("progress"));
        click();
        assertEquals(0, papers);
        assertFalse(waiting);
        assertFalse(effects.contains("progress"));
    }

    @Test public void failedStruggleKeepsPaperStillWhileAddingTheNextTest() {
        startSpecial(8); click(); finishTest(false);
        assertEquals(Arrays.asList("show", "hide", "show"), paperTransitions);
        assertArrayEquals(new String[]{"[#BAD]Observation-1[]"}, buttons);
        assertEquals(1, testCount());
        click();
        assertEquals(Arrays.asList("show", "hide", "show", "hide"), paperTransitions);
        assertEquals(2, testCount());
    }

    @Test public void allSpecialCardsStartWithTypewriterFlavorAndWaitForTestButton() {
        String[] firstTests = {"[#BAD]Strength-1[]", "[#BAD]Lore-1[]", "[#BAD]Observation-1[]",
                "[#BAD]Will-1[]", "Will", "Observation", "Observation", "[#BAD]Strength-1[]"};
        for (int card = 1; card <= 8; card++) {
            YigEncounterPresentationTest fixture = new YigEncounterPresentationTest();
            fixture.setup();
            fixture.startSpecial(card);
            assertEquals("Card " + card, Arrays.asList("showKnyanBackground", "flavor"), fixture.effects);
            assertTrue(fixture.infos.isEmpty());
            assertTrue(fixture.paperVisible);
            assertArrayEquals(new String[]{firstTests[card-1]}, fixture.buttons);
            assertNull("No dice roll before clicking", fixture.test);
            fixture.click();
            assertNotNull(fixture.test);
            fixture.cleanup();
        }
    }

    @Test public void specialSecondStageWaitsForItsOwnButtonAndRewardConfirmation() {
        startSpecial(1); click(); finishTest(true);
        assertArrayEquals(new String[]{"Lore"}, buttons);
        assertEquals(1, testCount());
        assertEquals(2, Collections.frequency(effects, "flavor"));
        assertFalse(effects.contains("progress"));
        assertFalse(allInfo().contains("Advance the Active Mystery"));
        click(); finishTest(true);
        assertEquals(2, testCount());
        assertTrue(allInfo().contains("[#GOOD]Advance the Active Mystery by 1.[]"));
        assertFalse(effects.contains("progress"));
        click();
        assertTrue(effects.contains("progress"));
        assertEquals("hideBackground", effects.get(effects.size()-1));
        assertEquals(0, papers);
        assertFalse(waiting);
    }

    @Test public void specialFailureRevealsPenaltyBeforeContinuingToSecondTest() {
        startSpecial(1); click(); finishTest(false);
        assertTrue(allInfo().contains("[#BAD]Lose 1 Health.[]"));
        assertFalse(effects.contains("loseHealth"));
        assertEquals(1, testCount());
        click();
        assertTrue(effects.contains("loseHealth"));
        assertArrayEquals(new String[]{"[#BAD]Will-1[]"}, buttons);
        assertEquals(1, testCount());
        click(); finishTest(false);
        assertTrue(allInfo().contains("[#BAD]Advance Doom by 1.[]"));
        assertFalse(effects.contains("advanceDoom"));
        click();
        assertTrue(effects.contains("advanceDoom"));
        assertEquals(0, papers);
        assertFalse(waiting);
    }

    @Test public void specialChoiceIsShownOnPaperAndPenaltyWaitsForConfirmation() {
        startSpecial(2); click(); finishTest(true);
        assertTrue(allInfo().contains("Spend 1 Clue"));
        assertArrayEquals(new String[]{"[#BAD]No[]", "[#GOOD]Yes[]"}, buttons);
        click(); // Decline the payment.
        assertTrue(infos.contains("[#BAD]Gain Paranoia.[]"));
        assertFalse(effects.contains("gainCondition:" + ConditionId.PARANOIA));
        click();
        assertTrue(effects.contains("gainCondition:" + ConditionId.PARANOIA));
        assertEquals(0, papers);
        assertFalse(waiting);
    }

    @Test public void specialEscapePenaltyAppearsOnlyAfterTheSecondTestFails() {
        startSpecial(5); click(); finishTest(true);
        assertArrayEquals(new String[]{"[#BAD]Influence-1[]"}, buttons);
        assertFalse(allInfo().contains("adjacent space"));
        click(); finishTest(false);
        assertTrue(infos.contains("[#BAD]Move to an adjacent space and become Delayed.[]"));
        assertArrayEquals(new String[]{"OK"}, buttons);
        assertFalse(effects.contains("progress"));
    }

    @Test public void successIsHiddenUntilTestFinishesAndAppliedAfterConfirmation() {
        start(2, LocationType.CITY);
        assertEquals(Collections.singletonList("flavor"), effects);
        assertTrue(infos.isEmpty());
        assertArrayEquals(new String[]{"[#BAD]Will-1[]"}, buttons);
        click();
        assertTrue(infos.isEmpty());
        assertNotNull(test);
        finishTest(true);
        assertTrue(infos.contains("[#GOOD]Gain this Clue.[]"));
        assertFalse(allInfo().contains("Hallucinations"));
        assertFalse(effects.contains("gainClueFromSpace"));
        click();
        assertTrue(effects.contains("gainClueFromSpace"));
        assertFalse(waiting);
        assertEquals(0, papers);
    }

    @Test public void failureShowsOnlyRedPenaltyAndAppliesItAfterConfirmation() {
        start(2, LocationType.CITY); click(); finishTest(false);
        assertTrue(infos.contains("[#BAD]Gain Hallucinations.[]"));
        assertFalse(allInfo().contains("Gain this Clue"));
        assertFalse(effects.contains("gainCondition:" + ConditionId.HALLUCINATIONS));
        click();
        assertTrue(effects.contains("gainCondition:" + ConditionId.HALLUCINATIONS));
        assertFalse(effects.contains("gainClueFromSpace"));
    }

    @Test public void rerollEncounterUsesTestButtonWithoutRevealingEitherOutcome() {
        start(21, LocationType.CITY);
        assertTrue(allInfo().contains("reroll up to 2 dice"));
        assertFalse(allInfo().contains("Clue"));
        assertFalse(allInfo().contains("Amnesia"));
        click();
        assertArrayEquals(new String[]{"Observation"}, buttons);
        click(); finishTest(false);
        assertTrue(allInfo().contains("[#BAD]Gain Amnesia, Hallucinations, and Paranoia.[]"));
        assertFalse(allInfo().contains("Gain this Clue"));
        click();
        assertTrue(effects.contains("gainCondition:" + ConditionId.AMNESIA));
    }

    @Test public void combatHidesRewardUntilTheMonsterIsDefeated() {
        start(4, LocationType.CITY);
        assertEquals("[#BAD]A Cultist ambushes you.[]", allInfo());
        click();
        assertNotNull(combat);
        CultistMonster monster = new CultistMonster(); monster.setCurrentHealth(0);
        CombatData result = new CombatData(); result.setMonsterInfo(monster);
        resume(() -> combat.onSuccess(result));
        assertTrue(allInfo().contains("[#GOOD]Gain this Clue.[]"));
        assertFalse(effects.contains("gainClueFromSpace"));
        click();
        assertTrue(effects.contains("gainClueFromSpace"));
    }

    @Test public void postTestPenaltyStaysAfterTheRewardAndUsesRed() {
        start(9, LocationType.WILDERNESS);
        assertTrue(infos.isEmpty()); click(); finishTest(true);
        assertTrue(allInfo().contains("[#GOOD]Gain this Clue and 1 additional Clue.[]"));
        assertFalse(allInfo().contains("Lose 2 Health"));
        click();
        assertTrue(effects.contains("gainClueFromSpace"));
        assertTrue(effects.contains("gainClueFromPool"));
        assertTrue(allInfo().contains("[#BAD]Lose 2 Health.[]"));
        assertFalse(effects.contains("loseHealth"));
        click();
        assertTrue(effects.contains("loseHealth"));
        assertEquals(0, papers);
    }

    @Test public void emptyFailureBranchStillCompletesAndRunsPostTestPenalty() {
        start(9, LocationType.WILDERNESS); click(); finishTest(false);
        assertFalse(allInfo().contains("Gain this Clue"));
        assertTrue(allInfo().contains("[#BAD]Lose 2 Health.[]"));
        click();
        assertTrue(effects.contains("loseHealth"));
        assertFalse(waiting);
    }

    private void start(int page, LocationType type) {
        YigResearchEncounter encounter = new YigResearchEncounter(page, type);
        encounter.setLocationId(LocationId.THE_AMAZON);
        encounter.executeWhole(); drain();
    }
    private void startSpecial(int card) {
        List<Integer> deck = new ArrayList<>(Collections.singletonList(card));
        MysteryCardInfo mystery = mock(MysteryCardInfo.class, (name, args) -> {
            assertEquals("getSpecialEncounterDeck", name); return deck;
        });
        new YigSpecialEncounter(mystery, () -> effects.add("progress")).execute();
        drain();
    }
    private int testCount() {
        int count = 0;
        for (String effect : effects) if (effect.startsWith("test:")) count++;
        return count;
    }
    private void click() {
        SingleSubscriber<? super Integer> current = button; button = null;
        assertNotNull(current); resume(() -> current.onSuccess(0));
    }
    private void finishTest(boolean passed) {
        TestData result = new TestData(); result.setDiceRolls(Collections.emptyList());
        result.setScoreBonus(passed ? 1 : 0); result.setMinScoreToBeSuccessful(1);
        resume(() -> test.onSuccess(result));
    }
    private String allInfo() { return String.join("\n", infos); }
    private void queue(Runnable action) { (inserted == null ? pending : inserted).add(action); }
    private void resume(Runnable action) {
        waiting = false; inserted = new ArrayList<>(); action.run();
        pending.addAll(0, inserted); inserted = null; drain();
    }
    private void drain() {
        int limit = 1000;
        while (!waiting && !pending.isEmpty()) {
            if (--limit == 0) throw new AssertionError("Command loop");
            Runnable action = pending.remove(0); inserted = new ArrayList<>(); action.run();
            pending.addAll(0, inserted); inserted = null;
        }
    }
    private <T> T mock(Class<T> type, BiFunction<String, Object[], Object> handler) {
        return type.cast(Proxy.newProxyInstance(type.getClassLoader(), new Class[]{type},
                (proxy, method, args) -> handler.apply(method.getName(), args)));
    }
}
