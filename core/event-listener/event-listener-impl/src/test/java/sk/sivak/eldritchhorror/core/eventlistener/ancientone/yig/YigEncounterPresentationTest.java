package sk.sivak.eldritchhorror.core.eventlistener.ancientone.yig;

import java.lang.reflect.Proxy;
import java.util.*;
import java.util.function.BiFunction;
import java8.features.function.Consumer;
import org.junit.*;
import rx.Single;
import rx.SingleSubscriber;
import sk.sivak.eldritchhorror.core.constants.MysteryCardInfo;
import sk.sivak.eldritchhorror.core.constants.ancientone.AncientOneId;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.investigator.*;
import sk.sivak.eldritchhorror.core.constants.location.*;
import sk.sivak.eldritchhorror.core.constants.monster.CultistMonster;
import sk.sivak.eldritchhorror.core.constants.question.Answer;
import sk.sivak.eldritchhorror.core.constants.question.Question;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.builder.ResearchEncounterTextBuilder;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.research.yig.YigResearchEncounter;
import sk.sivak.eldritchhorror.core.eventqueue.EventQueueImpl;
import sk.sivak.eldritchhorror.core.eventtype.data.combat.CombatData;
import sk.sivak.eldritchhorror.core.eventtype.data.test.TestData;
import sk.sivak.eldritchhorror.core.eventtype.data.token.SpendData;
import sk.sivak.eldritchhorror.core.model.*;
import sk.sivak.eldritchhorror.core.service.*;
import static org.junit.Assert.*;

/** Pauses the command queue at buttons, tests, and combat, as the real UI does. */
public class YigEncounterPresentationTest {
    private final List<Runnable> pending = new ArrayList<>();
    private List<Runnable> inserted;
    private final List<String> infos = new ArrayList<>(), effects = new ArrayList<>();
    private final List<String> flavors = new ArrayList<>();
    private final List<String> paperTransitions = new ArrayList<>();
    private SingleSubscriber<? super Integer> button;
    private SingleSubscriber<? super TestData> test;
    private SingleSubscriber<? super CombatData> combat;
    private SingleSubscriber<? super Answer> question;
    private String[] buttons;
    private boolean waiting;
    private int papers;
    private boolean paperVisible;
    private ServicePlatform platform;

    @Before public void setup() {
        ServicePlatform.nullifyInstance();
        platform = ServicePlatform.get();
        setYigAwake(false);
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
            if (name.equals("typeFlavor")) queue(() -> {
                assertTrue("Narrative must be on visible paper", paperVisible);
                assertNotNull(args[0]);
                assertFalse("Narrative must not be empty", ((String) args[0]).trim().isEmpty());
                flavors.add((String) args[0]); effects.add("flavor");
            });
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
            if (name.equals("ask")) return Single.<Answer>create(sub -> queue(() -> {
                question = sub; waiting = true;
            }));
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
        platform.setInvestigators(mock(InvestigatorsRead.class, (name, args) ->
                name.equals("getOnBoardInvestigators") || name.equals("getDefeatedInvestigators")
                        ? Collections.emptyList() : investigator));
        platform.setConditionsDeck(mock(ConditionsDeckRead.class, (name, args) -> false));
        platform.setArtifactsDeck(mock(ArtifactsDeckRead.class, (name, args) -> Collections.emptyList()));
        platform.setAssetDeck(mock(AssetDeckRead.class, (name, args) -> Collections.emptyList()));
        platform.setCluePoolRead(mock(CluePoolRead.class, (name, args) -> Collections.emptyList()));
    }
    @After public void cleanup() { ServicePlatform.nullifyInstance(); }

    @Test public void sleepingYigDoesNotShowOrApplyAwakenedHealthLoss() {
        researchHealthLoss(false);
    }

    @Test public void awakenedYigShowsDirectHealthLossAndAppliesItAfterChoice() {
        researchHealthLoss(true);
    }

    private void researchHealthLoss(boolean awake) {
        setYigAwake(awake);
        start(6, LocationType.WILDERNESS);
        assertTrue(allInfo().contains("spend 2 Health"));
        assertFalse(allInfo().contains("[#BAD]Lose 2 Health.[]"));
        assertFalse(allInfo().contains("If Yig is awake"));
        assertFalse(effects.contains("loseHealth"));
        click(); // Decline the optional payment; the awakened penalty still applies.
        assertEquals(2, flavors.size());
        assertFalse(effects.contains("loseHealth"));
        click(); // Acknowledge the declined ritual.
        if (awake) {
            assertTrue(allInfo().contains("[#BAD]Lose 2 Health.[]"));
            assertEquals(3, flavors.size());
            assertFalse(effects.contains("loseHealth"));
            click();
        }
        assertEquals(awake, effects.contains("loseHealth"));
        assertFalse(waiting);
        assertEquals(0, papers);
    }

    private void setYigAwake(boolean awake) {
        platform.setModel(mock(ModelRead.class, (name, args) ->
                mock(AncientOneRead.class, (n, a) ->
                        mock(sk.sivak.eldritchhorror.core.constants.ancientone.AncientOneInfo.class,
                                (nn, aa) -> awake))));
    }

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
        assertTrue(allInfo().contains("[#GOOD]Advance the Active Mystery.[]"));
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
        assertEquals(2, flavors.size());
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
        assertEquals(2, flavors.size());
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

    @Test public void emptyFailureBranchHasNarrativeBeforePostTestPenalty() {
        start(9, LocationType.WILDERNESS); click(); finishTest(false);
        assertFalse(allInfo().contains("Gain this Clue"));
        assertEquals(2, flavors.size());
        assertFalse(allInfo().contains("[#BAD]Lose 2 Health.[]"));
        click();
        assertTrue(allInfo().contains("[#BAD]Lose 2 Health.[]"));
        assertEquals(3, flavors.size());
        click();
        assertTrue(effects.contains("loseHealth"));
        assertFalse(waiting);
    }

    @Test public void optionalDarkPactDeclineHasNarrativeWithoutAnEffect() {
        allowDarkPact(false);
        start(7, LocationType.CITY);
        assertTrue(allInfo().contains("Dark Pact"));
        assertArrayEquals(new String[]{"[#BAD]No[]", "[#GOOD]Yes[]"}, buttons);
        assertEquals(Collections.singletonList("flavor"), effects);
        click();
        assertEquals(Arrays.asList("flavor", "flavor"), effects);
        assertArrayEquals(new String[]{"OK"}, buttons);
        click();
        assertEquals(0, papers);
        assertFalse(waiting);
    }

    @Test public void acceptingDarkPactNarratesAfterCostAndBeforeReward() {
        allowDarkPact(true);
        start(7, LocationType.CITY);
        assertFalse(effects.contains("gainClueFromSpace"));
        click(1);
        assertEquals(Arrays.asList("flavor", "gainCondition:DARK_PACT", "flavor"), effects);
        assertArrayEquals(new String[]{"OK"}, buttons);
        click();
        assertEquals(Arrays.asList("flavor", "gainCondition:DARK_PACT", "flavor", "hideBackground", "gainClueFromSpace"), effects);
        assertEquals(0, papers);
        assertFalse(waiting);
    }

    @Test public void missingDarkPactDoesNotGrantRewardAfterAccepting() {
        allowDarkPact(false);
        start(7, LocationType.CITY); click(1);
        assertTrue(effects.contains("gainCondition:DARK_PACT"));
        assertFalse(effects.contains("gainClueFromSpace"));
        assertEquals(2, flavors.size());
        click();
        assertEquals(0, papers);
    }

    @Test public void allOptionalOpeningChoicesUseYesNoOnTheEncounterPaper() {
        int[][] cards = {{7, 23}, {6, 8, 21}, {3, 12}};
        LocationType[] types = {LocationType.CITY, LocationType.WILDERNESS, LocationType.SEA};
        for (int i = 0; i < types.length; i++) for (int card : cards[i]) {
            YigEncounterPresentationTest fixture = new YigEncounterPresentationTest();
            fixture.setup(); fixture.allowDarkPact(false); fixture.start(card, types[i]);
            assertArrayEquals(types[i] + " " + card,
                    new String[]{"[#BAD]No[]", "[#GOOD]Yes[]"}, fixture.buttons);
            assertFalse(fixture.allInfo().isEmpty());
            assertTrue(fixture.paperVisible);
            fixture.cleanup();
        }
    }

    @Test public void unavailableDarkPactStillExplainsEncounterBeforeEnding() {
        start(7, LocationType.CITY);
        assertTrue(allInfo().contains("Dark Pact"));
        assertArrayEquals(new String[]{"OK"}, buttons);
        click();
        assertEquals(2, flavors.size());
        click();
        assertEquals(0, papers);
        assertFalse(effects.contains("gainClueFromSpace"));
    }

    @Test public void improvementPaymentWithoutTokensOnlyExplainsAndDetains() {
        start(15, LocationType.CITY);
        assertTrue(allInfo().contains("improvement token"));
        assertArrayEquals(new String[]{"OK"}, buttons);
        click();
        assertFalse(effects.contains("gainCondition:DETAINED"));
        assertEquals(2, flavors.size());
        click();
        assertTrue(effects.contains("gainCondition:DETAINED"));
        assertEquals(0, papers);
    }

    @Test public void improvementPaymentUsesShortYesNoButtonsOnThePaper() {
        for (boolean accept : new boolean[]{false, true}) {
            YigEncounterPresentationTest fixture = new YigEncounterPresentationTest();
            fixture.setup();
            List<String> spent = fixture.withImprovement(Stat.LORE);
            fixture.start(15, LocationType.CITY);
            assertArrayEquals(new String[]{"[#BAD]No[]", "[#GOOD]Yes[]"}, fixture.buttons);
            fixture.click(accept ? 1 : 0);
            assertEquals(2, fixture.flavors.size());
            fixture.click();
            assertEquals(!accept, fixture.effects.contains("gainCondition:DETAINED"));
            // A single improved skill needs no extra picker or long cancel button.
            assertEquals(accept ? Collections.singletonList("LORE") : Collections.emptyList(), spent);
            assertFalse(fixture.effects.stream().anyMatch(e -> e.startsWith("ask:")));
            assertEquals(0, fixture.papers);
            fixture.cleanup();
        }
    }

    /** Skills/modifiers independently transcribed from the wiki's 24-card table. */
    @Test public void allSkillTestsMatchWikiAndRevealOnlyTheirResolvedNarrative() {
        String[][] rules = {
                {"CITY", "1:OBSERVATION:-1 2:WILL:-1 3:OBSERVATION:-1 5:INFLUENCE:-1 6:LORE:0 8:LORE:-1 10:OBSERVATION:-2 11:LORE:-2 12:OBSERVATION:-1 13:WILL:0 14:OBSERVATION:-1 17:LORE:-1 19:OBSERVATION:-1 20:OBSERVATION:-1 21:OBSERVATION:0 24:OBSERVATION:0"},
                {"WILDERNESS", "1:STRENGTH:-3 2:LORE:-1 3:OBSERVATION:-2 4:STRENGTH:0 5:OBSERVATION:-2 9:OBSERVATION:-2 10:OBSERVATION:-1 11:OBSERVATION:0 12:OBSERVATION:0 13:WILL:-1 14:LORE:0 16:OBSERVATION:-1 18:INFLUENCE:-1 19:INFLUENCE:-1 20:OBSERVATION:-2 22:OBSERVATION:-1 23:OBSERVATION:-2 24:OBSERVATION:-1"},
                {"SEA", "1:OBSERVATION:-1 2:OBSERVATION:-1 4:LORE:0 5:INFLUENCE:-1 6:INFLUENCE:-1 8:INFLUENCE:-1 9:LORE:-2 10:OBSERVATION:-1 14:OBSERVATION:-1 16:WILL:-1 17:OBSERVATION:-1 18:OBSERVATION:-1 19:OBSERVATION:-1 20:OBSERVATION:-1 21:OBSERVATION:0 22:OBSERVATION:0 23:INFLUENCE:-1 24:STRENGTH:-1"}
        };
        for (String[] group : rules) for (String row : group[1].split(" ")) for (boolean passed : new boolean[]{false, true}) {
            String[] rule = row.split(":");
            int page = Integer.parseInt(rule[0]);
            LocationType type = LocationType.valueOf(group[0]);
            YigEncounterPresentationTest fixture = new YigEncounterPresentationTest();
            fixture.setup(); fixture.start(page, type); fixture.beginTest();
            assertTrue(group[0] + " " + row, fixture.effects.contains("test:" + rule[1] + ":" + rule[2]));
            assertEquals(1, fixture.flavors.size());
            fixture.finishTest(passed);
            ResearchEncounterTextBuilder text = new ResearchEncounterTextBuilder(page, type, AncientOneId.YIG);
            String expected = (passed ? text.withPass() : text.withFail()).withFlavor().build();
            assertEquals(group[0] + " " + row, Arrays.asList(
                    text.withFlavor().build(), "\n" + expected), fixture.flavors);
            assertArrayEquals(new String[]{"OK"}, fixture.buttons);
            assertTrue(fixture.paperVisible);
            fixture.cleanup();
        }
    }

    @Test public void conditionalModifiersStillMatchWiki() {
        assertModifier(6, LocationType.CITY, false, false, 2, LocationId.LONDON, "LORE:-4");
        assertModifier(1, LocationType.WILDERNESS, false, false, 0, LocationId.LONDON, "STRENGTH:-1");
        assertModifier(4, LocationType.WILDERNESS, true, false, 0, LocationId.THE_AMAZON, "STRENGTH:-2");
        assertModifier(13, LocationType.WILDERNESS, true, false, 0, LocationId.THE_AMAZON, "WILL:-2");
        assertModifier(24, LocationType.SEA, false, true, 0, LocationId.LONDON, "STRENGTH:-3");
    }

    private void assertModifier(int page, LocationType type, boolean poisoned, boolean awake,
                                int artifacts, LocationId location, String expected) {
        YigEncounterPresentationTest fixture = new YigEncounterPresentationTest(); fixture.setup();
        fixture.setYigAwake(awake);
        fixture.platform.setConditionsDeck(mock(ConditionsDeckRead.class, (name, args) ->
                name.equals("hasCondition") && args[1] == ConditionId.POISONED && poisoned));
        fixture.platform.setArtifactsDeck(mock(ArtifactsDeckRead.class, (name, args) -> Collections.nCopies(artifacts, null)));
        fixture.start(page, type, location); fixture.beginTest();
        assertTrue(fixture.effects.contains("test:" + expected));
        fixture.cleanup();
    }

    @Test public void successesWithoutAClueDoNotInventOne() {
        int[] pages = {6,17,13,23,9,18};
        LocationType[] types = {LocationType.CITY,LocationType.CITY,LocationType.WILDERNESS,
                LocationType.WILDERNESS,LocationType.SEA,LocationType.SEA};
        String[] rewards = {"gainCondition:BLESSED", "gainArtifact:TOME", "gainSanity",
                "gainArtifact:ELIXIR_OF_LIFE", "gainArtifact:ZANTHU_TABLETS", "retreatDoom"};
        for (int i = 0; i < pages.length; i++) {
            YigEncounterPresentationTest fixture = new YigEncounterPresentationTest(); fixture.setup();
            fixture.start(pages[i], types[i]); fixture.beginTest(); fixture.finishTest(true);
            assertEquals(2, fixture.flavors.size());
            assertFalse(fixture.effects.contains(rewards[i]));
            fixture.click();
            assertTrue(rewards[i], fixture.effects.contains(rewards[i]));
            assertFalse(fixture.effects.contains("gainClueFromSpace"));
            assertFalse(fixture.effects.contains("gainClueFromPool"));
            assertEquals(0, fixture.papers);
            fixture.cleanup();
        }
    }

    @Test public void failuresWithoutEffectsStillWaitForTheirEnding() {
        int[] pages = {20,5,9,10,24,8};
        LocationType[] types = {LocationType.CITY,LocationType.WILDERNESS,LocationType.WILDERNESS,
                LocationType.WILDERNESS,LocationType.WILDERNESS,LocationType.SEA};
        for (int i = 0; i < pages.length; i++) {
            YigEncounterPresentationTest fixture = new YigEncounterPresentationTest(); fixture.setup();
            fixture.start(pages[i], types[i], LocationId.LONDON); fixture.beginTest();
            List<String> before = new ArrayList<>(fixture.effects);
            fixture.finishTest(false);
            assertEquals(2, fixture.flavors.size());
            assertTrue(fixture.waiting); assertEquals(1, fixture.papers);
            fixture.click(); before.add("flavor");
            assertEquals(before, fixture.effects);
            assertFalse(fixture.waiting); assertEquals(0, fixture.papers);
            fixture.cleanup();
        }
    }

    @Test public void independentCombatRewardsAndPenaltiesRetainAllCombinations() {
        int[] pages = {16,18,17,15};
        LocationType[] types = {LocationType.CITY,LocationType.CITY,LocationType.WILDERNESS,LocationType.SEA};
        for (int i = 0; i < pages.length; i++) for (int mask = 0; mask < 16; mask++) {
            boolean will = (mask & 1) != 0, strength = (mask & 2) != 0;
            boolean lostHealth = (mask & 4) != 0, defeated = (mask & 8) != 0;
            YigEncounterPresentationTest fixture = new YigEncounterPresentationTest(); fixture.setup();
            fixture.start(pages[i], types[i]); fixture.click();
            assertNotNull(fixture.combat);
            fixture.finishCombat(will, strength, lostHealth, defeated);
            assertTrue(fixture.paperVisible); assertEquals(2, fixture.flavors.size());
            assertArrayEquals(new String[]{"OK"}, fixture.buttons);
            // City 18 grants its clue before combat. All other rewards wait for this ending.
            assertEquals(i == 1 ? 1 : 0, Collections.frequency(fixture.effects, "gainClueFromSpace"));
            fixture.click();
            boolean clue = i == 0 ? defeated : i == 1 || (i == 2 ? will || strength : will);
            assertEquals(types[i] + " " + pages[i] + " combination " + mask,
                    clue ? 1 : 0, Collections.frequency(fixture.effects, "gainClueFromSpace"));
            assertEquals(i == 2 && will && strength ? 1 : 0, Collections.frequency(fixture.effects, "gainClueFromPool"));
            assertEquals(i == 0 && !will, fixture.effects.contains("gainCondition:CURSED"));
            assertEquals((i == 1 || i == 3) && lostHealth, fixture.effects.contains("gainCondition:POISONED"));
            assertFalse(fixture.waiting); assertEquals(0, fixture.papers);
            fixture.cleanup();
        }
    }

    @Test public void wildernessCultistClueDependsOnPoisonNotVictory() {
        for (boolean poisoned : new boolean[]{false, true}) for (boolean defeated : new boolean[]{false, true}) {
            YigEncounterPresentationTest fixture = new YigEncounterPresentationTest(); fixture.setup();
            fixture.platform.setConditionsDeck(mock(ConditionsDeckRead.class, (name, args) ->
                    name.equals("hasCondition") && args[1] == ConditionId.POISONED && poisoned));
            fixture.start(7, LocationType.WILDERNESS); fixture.click();
            fixture.finishCombat(true, true, false, defeated);
            assertEquals(2, fixture.flavors.size()); fixture.click();
            assertEquals(!poisoned, fixture.effects.contains("gainClueFromSpace"));
            assertEquals(0, fixture.papers); fixture.cleanup();
        }
    }

    @Test public void healthPaymentsNarrateTheActualResultAndNeverInventAReward() {
        for (LocationType type : new LocationType[]{LocationType.CITY, LocationType.WILDERNESS})
            for (boolean accept : new boolean[]{false, true}) for (boolean enough : new boolean[]{false, true}) {
                YigEncounterPresentationTest fixture = new YigEncounterPresentationTest(); fixture.setup();
                fixture.withPayment(enough);
                fixture.start(type == LocationType.CITY ? 23 : 6, type);
                fixture.click(accept ? 1 : 0);
                assertEquals(2, fixture.flavors.size());
                assertEquals(accept && enough, fixture.effects.contains("pay:0:2:0"));
                assertFalse(fixture.effects.contains("gainClueFromSpace"));
                fixture.click();
                assertEquals(accept && enough, fixture.effects.contains("gainClueFromSpace"));
                assertEquals(0, fixture.papers); fixture.cleanup();
            }
    }

    @Test public void wildernessTwelveRequiresBothSuccessfulTestAndHealthPayment() {
        for (boolean accept : new boolean[]{false, true}) for (boolean enough : new boolean[]{false, true}) {
            YigEncounterPresentationTest fixture = new YigEncounterPresentationTest(); fixture.setup();
            fixture.withPayment(enough);
            fixture.start(12, LocationType.WILDERNESS); fixture.beginTest(); fixture.finishTest(true); fixture.click();
            fixture.answer(accept);
            assertEquals(3, fixture.flavors.size());
            assertFalse(fixture.effects.contains("gainClueFromSpace"));
            fixture.click();
            assertEquals(accept && enough, fixture.effects.contains("gainClueFromSpace"));
            assertEquals(!(accept && enough), fixture.effects.contains("gainCondition:CURSED"));
            assertEquals(0, fixture.papers); fixture.cleanup();
        }
    }

    @Test public void seaPactPaysSanityThenGrantsTwoDifferentImprovementsWithoutAClue() {
        allowDarkPact(true); withPayment(true); withImprovement(Stat.LORE);
        platform.setGameService(mock(GameService.class, (name, args) -> {
            if (name.equals("ask")) {
                Question<?> choice = (Question<?>) args[0];
                Answer<Object,Object> answer = new Answer<>();
                answer.setResponseData(choice.getOptions().get(0).getValue());
                return Single.just(answer);
            }
            queue(() -> effects.add(name + ":" + args[0])); return null;
        }));
        platform.setInvestigatorService(mock(InvestigatorService.class, (name, args) -> {
            queue(() -> effects.add(name + ":" + args[0])); return null;
        }));
        start(3, LocationType.SEA); click(1);
        assertEquals(Arrays.asList("flavor", "pay:0:0:2", "gainCondition:DARK_PACT", "flavor"), effects);
        click();
        assertTrue(effects.contains("improveSkill:LORE"));
        assertTrue(effects.contains("improveSkill:INFLUENCE"));
        assertFalse(effects.contains("gainClueFromSpace"));
        assertEquals(0, papers);
    }

    private void withPayment(boolean enough) {
        platform.setTokenService(mock(TokenService.class, (name, args) -> {
            if (name.equals("spend")) {
                SpendData cost = new SpendData(); cost.setHasEnough(enough);
                cost.setPayAction(() -> queue(() -> effects.add("pay:" + args[0] + ":" + args[2] + ":" + args[3])));
                return Single.just(cost);
            }
            queue(() -> effects.add(name)); return null;
        }));
    }

    private void answer(boolean yes) {
        Answer<Boolean,Object> answer = new Answer<>(); answer.setResponseData(yes);
        SingleSubscriber<? super Answer> current = question; question = null;
        assertNotNull(current); resume(() -> current.onSuccess(answer));
    }

    private void beginTest() {
        for (int i = 0; i < 3 && test == null; i++) click();
        assertNotNull(test);
    }

    private void finishCombat(boolean will, boolean strength, boolean lostHealth, boolean defeated) {
        CultistMonster monster = new CultistMonster(); monster.setCurrentHealth(defeated ? 0 : 1);
        CombatData result = new CombatData(); result.setMonsterInfo(monster);
        result.setHorrorTestResult(testResult(will)); result.setDamageTestResult(testResult(strength));
        result.setHealthLost(lostHealth ? 1 : 0);
        resume(() -> combat.onSuccess(result));
    }

    private List<String> withImprovement(Stat improved) {
        List<String> spent = new ArrayList<>();
        InvestigatorInfo info = mock(InvestigatorInfo.class, (name, args) -> InvestigatorId.THE_SAILOR);
        InvestigatorRead investigator = mock(InvestigatorRead.class, (name, args) -> {
            if (name.equals("getInfo")) return info;
            if (name.equals("getCurrentLocationId")) return LocationId.THE_AMAZON;
            if (name.equals("getStatBonus")) return args[0] == improved ? 1 : 0;
            return 0;
        });
        platform.setInvestigators(mock(InvestigatorsRead.class, (name, args) ->
                name.equals("getOnBoardInvestigators") ? Collections.emptyList() : investigator));
        platform.setInvestigatorService(mock(InvestigatorService.class, (name, args) -> {
            if (name.equals("loseImprovement")) queue(() -> spent.add(
                    ((sk.sivak.eldritchhorror.core.eventtype.data.investigator.LoseImprovementData) args[0]).getStat().name()));
            return null;
        }));
        return spent;
    }

    private void allowDarkPact(boolean received) {
        platform.setConditionsDeck(mock(ConditionsDeckRead.class, (name, args) ->
                name.equals("canGetConditionId") || (name.equals("hasCondition")
                        && args[1] == ConditionId.DARK_PACT && received
                        && effects.contains("gainCondition:DARK_PACT"))));
        platform.setInvestigators(mock(InvestigatorsRead.class, (name, args) -> {
            if (name.equals("getOnBoardInvestigators")) return Collections.emptyList();
            return mock(InvestigatorRead.class, (n, a) -> n.equals("getInfo")
                    ? mock(InvestigatorInfo.class, (nn, aa) -> InvestigatorId.THE_SAILOR) : LocationId.THE_AMAZON);
        }));
    }

    private void start(int page, LocationType type) {
        start(page, type, LocationId.THE_AMAZON);
    }
    private void start(int page, LocationType type, LocationId location) {
        YigResearchEncounter encounter = new YigResearchEncounter(page, type);
        encounter.setLocationId(location);
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
        click(0);
    }
    private void click(int index) {
        SingleSubscriber<? super Integer> current = button; button = null;
        assertNotNull(current); resume(() -> current.onSuccess(index));
    }
    private void finishTest(boolean passed) {
        resume(() -> test.onSuccess(testResult(passed)));
    }
    private TestData testResult(boolean passed) {
        TestData result = new TestData(); result.setDiceRolls(Collections.emptyList());
        result.setScoreBonus(passed ? 1 : 0); result.setMinScoreToBeSuccessful(1);
        return result;
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
