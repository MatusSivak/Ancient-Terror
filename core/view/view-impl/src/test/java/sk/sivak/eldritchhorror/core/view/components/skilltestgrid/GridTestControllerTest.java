package sk.sivak.eldritchhorror.core.view.components.skilltestgrid;

import org.junit.Test;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;
import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class GridTestControllerTest {

    @Test
    public void oneOneOneMatchAwardsNoSuccess() {
        GridTestController controller = createController(new QueueSymbolProvider(
                SymbolType.FIVE, SymbolType.TWO, SymbolType.THREE
        ));
        controller.setDebugBoard(
                SymbolType.ONE, SymbolType.ONE, SymbolType.ONE,
                SymbolType.TWO, SymbolType.FIVE, SymbolType.THREE,
                SymbolType.THREE, SymbolType.SIX, SymbolType.TWO
        );
        List<GridMatch> matches = controller.findMatches();
        assertEquals(1, matches.size());

        MatchResolution resolution = controller.resolveMatches(matches);
        assertEquals(0, resolution.getSuccessesGained());
        assertEquals(1, resolution.getMatchedLines());
        assertEquals(0, controller.getSuccesses());
    }

    @Test
    public void twoTwoTwoMatchAwardsNoSuccess() {
        GridTestController controller = createController(new QueueSymbolProvider(
                SymbolType.FIVE, SymbolType.THREE, SymbolType.ONE
        ));
        controller.setDebugBoard(
                SymbolType.TWO, SymbolType.TWO, SymbolType.TWO,
                SymbolType.ONE, SymbolType.FIVE, SymbolType.THREE,
                SymbolType.THREE, SymbolType.SIX, SymbolType.ONE
        );
        MatchResolution resolution = controller.resolveMatches(controller.findMatches());
        assertEquals(0, resolution.getSuccessesGained());
        assertEquals(1, resolution.getMatchedLines());
        assertEquals(0, controller.getSuccesses());
    }

    @Test
    public void threeThreeThreeMatchAwardsNoSuccess() {
        GridTestController controller = createController(new QueueSymbolProvider(
                SymbolType.FIVE, SymbolType.ONE, SymbolType.TWO
        ));
        controller.setDebugBoard(
                SymbolType.THREE, SymbolType.THREE, SymbolType.THREE,
                SymbolType.ONE, SymbolType.FIVE, SymbolType.TWO,
                SymbolType.TWO, SymbolType.SIX, SymbolType.ONE
        );
        MatchResolution resolution = controller.resolveMatches(controller.findMatches());
        assertEquals(0, resolution.getSuccessesGained());
        assertEquals(1, resolution.getMatchedLines());
        assertEquals(0, controller.getSuccesses());
    }

    @Test
    public void fiveFiveFiveAndSixSixSixAwardSuccess() {
        GridTestController controller555 = createController(new QueueSymbolProvider(
                SymbolType.ONE, SymbolType.TWO, SymbolType.THREE
        ));
        controller555.setDebugBoard(
                SymbolType.FIVE, SymbolType.FIVE, SymbolType.FIVE,
                SymbolType.ONE, SymbolType.TWO, SymbolType.THREE,
                SymbolType.TWO, SymbolType.THREE, SymbolType.ONE
        );
        MatchResolution resolution555 = controller555.resolveMatches(controller555.findMatches());
        assertEquals(1, resolution555.getSuccessesGained());

        GridTestController controller666 = createController(new QueueSymbolProvider(
                SymbolType.ONE, SymbolType.TWO, SymbolType.THREE
        ));
        controller666.setDebugBoard(
                SymbolType.SIX, SymbolType.SIX, SymbolType.SIX,
                SymbolType.ONE, SymbolType.TWO, SymbolType.THREE,
                SymbolType.TWO, SymbolType.THREE, SymbolType.ONE
        );
        MatchResolution resolution666 = controller666.resolveMatches(controller666.findMatches());
        assertEquals(1, resolution666.getSuccessesGained());
    }

    @Test
    public void mixedSimultaneousMatchesAwardOnlyScoringLines() {
        GridTestController controller = createController(new QueueSymbolProvider(
                SymbolType.ONE, SymbolType.ONE, SymbolType.ONE, SymbolType.ONE, SymbolType.ONE
        ));
        controller.setDebugBoard(
                SymbolType.FIVE, SymbolType.FIVE, SymbolType.FIVE,
                SymbolType.TWO, SymbolType.THREE, SymbolType.TWO,
                SymbolType.ONE, SymbolType.ONE, SymbolType.ONE
        );
        List<GridMatch> matches = controller.findMatches();
        assertEquals(2, matches.size());

        MatchResolution resolution = controller.resolveMatches(matches);
        assertEquals(2, resolution.getMatchedLines());
        assertEquals(1, resolution.getSuccessesGained());
        assertEquals(1, controller.getSuccesses());
    }

    @Test
    public void overlappingMatchesCountSeparatelyAndCellsReplaceOncePerWave() {
        CountingQueueProvider randomProvider = new CountingQueueProvider(
                SymbolType.ONE, SymbolType.ONE, SymbolType.ONE, SymbolType.ONE, SymbolType.ONE
        );
        GridTestController controller = createController(randomProvider);
        controller.setDebugBoard(
                SymbolType.FIVE, SymbolType.FIVE, SymbolType.FIVE,
                SymbolType.FIVE, SymbolType.ONE, SymbolType.TWO,
                SymbolType.FIVE, SymbolType.TWO, SymbolType.THREE
        );

        List<GridMatch> matches = controller.findMatches();
        assertEquals(2, matches.size());
        MatchResolution resolution = controller.resolveMatches(matches);
        assertEquals(2, resolution.getSuccessesGained());
        assertEquals(5, resolution.getReplacements().size());
        assertEquals(5, randomProvider.consumed);
    }

    @Test
    public void neutralThenScoringCascadeAwardsOnlyScoringSuccessesAndConsumesNoExtraMoves() {
        GridTestController controller = createController(new QueueSymbolProvider(
                SymbolType.ONE, SymbolType.TWO, SymbolType.THREE,
                SymbolType.FIVE, SymbolType.SIX, SymbolType.ONE,
                SymbolType.TWO, SymbolType.THREE, SymbolType.FIVE, // initial board generation
                SymbolType.FIVE, SymbolType.FIVE, SymbolType.FIVE, // refill for 111 -> 555
                SymbolType.ONE, SymbolType.TWO, SymbolType.THREE  // refill for 555 -> stable
        ));
        controller.startTest(3);
        controller.setDebugBoard(
                SymbolType.ONE, SymbolType.ONE, SymbolType.ONE,
                SymbolType.TWO, SymbolType.THREE, SymbolType.TWO,
                SymbolType.THREE, SymbolType.TWO, SymbolType.THREE
        );
        MatchResolution firstWave = controller.resolveMatches(controller.findMatches());
        assertEquals(0, firstWave.getSuccessesGained());
        MatchResolution secondWave = controller.resolveMatches(controller.findMatches());
        assertEquals(1, secondWave.getSuccessesGained());

        assertEquals(1, controller.getSuccesses());
        assertEquals(3, controller.getMovesRemaining());
    }

    @Test
    public void oneMoveIsConsumedExactlyOnceAndResolutionConsumesNoExtraMoves() {
        GridTestController controller = createController(new QueueSymbolProvider(
                SymbolType.ONE, SymbolType.TWO, SymbolType.THREE,
                SymbolType.FIVE, SymbolType.SIX, SymbolType.ONE,
                SymbolType.TWO, SymbolType.THREE, SymbolType.FIVE, // initial board generation
                SymbolType.FIVE, // incoming shift symbol
                SymbolType.SIX, SymbolType.TWO, SymbolType.THREE // replacements for 555
        ));
        controller.startTest(1);
        controller.setDebugBoard(
                SymbolType.TWO, SymbolType.FIVE, SymbolType.FIVE,
                SymbolType.ONE, SymbolType.THREE, SymbolType.ONE,
                SymbolType.ONE, SymbolType.TWO, SymbolType.THREE
        );
        controller.setState(GridTestState.WAITING_FOR_INPUT);
        controller.applyMove(new GridMove(GridMoveType.ROW_LEFT, 0));
        assertEquals(0, controller.getMovesRemaining());
        resolveAllMatches(controller);
        assertEquals(0, controller.getMovesRemaining());
    }

    @Test
    public void neutralMatchFromPlayerMoveAwardsBonusMove() {
        GridTestController controller = createController(new QueueSymbolProvider(
                SymbolType.ONE, SymbolType.TWO, SymbolType.THREE,
                SymbolType.FIVE, SymbolType.SIX, SymbolType.ONE,
                SymbolType.TWO, SymbolType.THREE, SymbolType.FIVE, // initial board generation
                SymbolType.ONE, // incoming shift symbol
                SymbolType.FIVE, SymbolType.TWO, SymbolType.THREE // replacements for 111
        ));
        controller.startTest(1);
        controller.setDebugBoard(
                SymbolType.TWO, SymbolType.ONE, SymbolType.ONE,
                SymbolType.FIVE, SymbolType.THREE, SymbolType.ONE,
                SymbolType.ONE, SymbolType.TWO, SymbolType.THREE
        );
        controller.setState(GridTestState.WAITING_FOR_INPUT);
        controller.applyMove(new GridMove(GridMoveType.ROW_LEFT, 0));

        assertEquals(0, controller.getMovesRemaining());
        MatchResolution firstWave = controller.resolveMatches(controller.findMatches());

        assertEquals(0, firstWave.getSuccessesGained());
        assertEquals(1, controller.getMovesRemaining());
    }

    @Test
    public void neutralCascadeFromPlayerMoveAwardsBonusMoveForEachWave() {
        GridTestController controller = createController(new QueueSymbolProvider(
                SymbolType.ONE, SymbolType.TWO, SymbolType.THREE,
                SymbolType.FIVE, SymbolType.SIX, SymbolType.ONE,
                SymbolType.TWO, SymbolType.THREE, SymbolType.FIVE, // initial board generation
                SymbolType.ONE, // incoming shift symbol
                SymbolType.TWO, SymbolType.TWO, SymbolType.TWO, // refill for 111 -> 222
                SymbolType.FIVE, SymbolType.THREE, SymbolType.SIX // refill for 222 -> stable
        ));
        controller.startTest(1);
        controller.setDebugBoard(
                SymbolType.TWO, SymbolType.ONE, SymbolType.ONE,
                SymbolType.FIVE, SymbolType.THREE, SymbolType.ONE,
                SymbolType.ONE, SymbolType.TWO, SymbolType.THREE
        );
        controller.setState(GridTestState.WAITING_FOR_INPUT);
        controller.applyMove(new GridMove(GridMoveType.ROW_LEFT, 0));

        assertEquals(0, controller.getMovesRemaining());

        MatchResolution firstWave = controller.resolveMatches(controller.findMatches());
        assertEquals(0, firstWave.getSuccessesGained());
        assertEquals(1, controller.getMovesRemaining());

        MatchResolution secondWave = controller.resolveMatches(controller.findMatches());
        assertEquals(0, secondWave.getSuccessesGained());
        assertEquals(2, controller.getMovesRemaining());

        resolveAllMatches(controller);
        assertEquals(2, controller.getMovesRemaining());
    }

    @Test
    public void finalMoveStillResolvesMatchesAndThenFinishes() {
        GridTestController controller = createController(new QueueSymbolProvider(
                SymbolType.ONE, SymbolType.TWO, SymbolType.THREE,
                SymbolType.FIVE, SymbolType.SIX, SymbolType.ONE,
                SymbolType.TWO, SymbolType.THREE, SymbolType.FIVE, // initial board generation
                SymbolType.FIVE, // shift produces 555
                SymbolType.SIX, SymbolType.TWO, SymbolType.THREE // replacements
        ));
        controller.setInitialPickupCount(0);
        controller.setInitialSwapCount(0);
        controller.setInitialSuperRerollCount(0);
        controller.startTest(1);
        controller.setDebugBoard(
                SymbolType.TWO, SymbolType.FIVE, SymbolType.FIVE,
                SymbolType.ONE, SymbolType.THREE, SymbolType.ONE,
                SymbolType.ONE, SymbolType.TWO, SymbolType.THREE
        );
        controller.setState(GridTestState.WAITING_FOR_INPUT);
        controller.applyMove(new GridMove(GridMoveType.ROW_LEFT, 0));
        resolveAllMatches(controller);
        assertTrue(controller.shouldFinishWhenStable());
        GridTestResult result = controller.finish();
        assertEquals(1, result.getMovesUsed());
    }

    @Test
    public void zeroMovesAndNoTacticalChargesFinishRegardlessOfBlindMode() {
        for (boolean blind : new boolean[] {false, true}) {
            GridTestController controller = createController(new QueueSymbolProvider());
            controller.setConfiguredBlindEnabled(blind);
            controller.setInitialPickupCount(0);
            controller.setInitialSwapCount(0);
            controller.setInitialSuperRerollCount(0);
            controller.startTest(0);

            assertTrue(controller.shouldFinishWhenStable());
        }
    }

    @Test
    public void eachAvailableTacticalActionKeepsZeroShiftTestOpenInEveryMode() {
        for (TestMode mode : TestMode.values()) {
            for (boolean momentum : new boolean[] {false, true}) {
                for (boolean blind : new boolean[] {false, true}) {
                    for (int action = 0; action < 3; action++) {
                        GridTestController controller = createController(new QueueSymbolProvider());
                        controller.setSelectedMode(mode);
                        controller.setConfiguredMomentum(momentum);
                        controller.setConfiguredBlindEnabled(blind);
                        controller.setInitialSwapCount(action == 0 ? 1 : 0);
                        controller.setInitialSuperRerollCount(action == 1 ? 1 : 0);
                        controller.setInitialPickupCount(action == 2 ? 1 : 0);
                        controller.startTest(0);
                        controller.setDebugBoard(
                                SymbolType.ONE, null, null,
                                null, null, null,
                                null, null, null
                        );

                        controller.setState(GridTestState.CHECKING_MATCHES);
                        assertFalse(controller.shouldFinishWhenStable());
                        controller.setState(GridTestState.WAITING_FOR_INPUT);
                        assertFalse(controller.shouldFinishWhenStable());
                        assertFalse(controller.canAcceptInput());
                        assertFalse(controller.canActivateReroll());
                        assertFalse(controller.beginRerollTargeting());

                        if (action == 0) {
                            assertTrue(controller.beginSwapSelection(new GridPosition(0, 0)));
                            MatchResolution resolution = controller.performSwap(new GridPosition(0, 0), new GridPosition(0, 1));
                            assertEquals(0, resolution.getMatchedLines());
                            assertEquals(0, controller.getSwapRemaining());
                            assertNull(controller.getBoard().getCell(0, 0));
                            assertEquals(SymbolType.ONE, controller.getBoard().getCell(0, 1));
                            assertEquals(GridTestState.CHECKING_MATCHES, controller.getState());
                        } else if (action == 1) {
                            assertTrue(controller.canUseSuperReroll());
                            assertEquals(1, controller.performSuperReroll(new FixedReroller(SymbolType.TWO)).size());
                            assertEquals(0, controller.getSuperRerollsRemaining());
                            assertEquals(SymbolType.TWO, controller.getBoard().getCell(0, 0));
                        } else {
                            assertTrue(controller.startPickupMode());
                            assertEquals(SymbolType.ONE, controller.pickupToken(0, 0));
                            assertEquals(0, controller.getPickupsAvailable());
                            assertEquals(SymbolType.ONE, controller.getNextToken());
                            assertNull(controller.getBoard().getCell(0, 0));
                        }
                        assertTrue(controller.findMatches().isEmpty());
                        assertTrue(controller.shouldFinishWhenStable());
                        assertEquals(0, controller.getMovesRemaining());
                        assertEquals(0, controller.finish().getMovesUsed());
                    }
                }
            }
        }
    }

    @Test
    public void unusableTacticalChargesDoNotPreventCompletion() {
        for (TestMode mode : TestMode.values()) {
            GridTestController empty = createController(new QueueSymbolProvider());
            empty.setSelectedMode(mode);
            empty.startTest(0);
            empty.setDebugBoard(new SymbolType[GridBoard.SIZE * GridBoard.SIZE]);
            empty.setState(GridTestState.CHECKING_MATCHES);
            assertTrue(empty.shouldFinishWhenStable());
            empty.setState(GridTestState.WAITING_FOR_INPUT);
            assertFalse(empty.beginSwapSelection(new GridPosition(0, 0)));
            assertFalse(empty.canUseSuperReroll());
            assertFalse(empty.canUsePickup());
            assertTrue(empty.shouldFinishWhenStable());
            assertEquals(3, empty.getSwapRemaining());
            assertEquals(1, empty.getSuperRerollsRemaining());
            assertEquals(1, empty.getPickupsAvailable());

            GridTestController scoringOnly = createController(new QueueSymbolProvider());
            scoringOnly.setSelectedMode(mode);
            scoringOnly.setInitialSwapCount(0);
            scoringOnly.setInitialPickupCount(0);
            scoringOnly.setInitialSuperRerollCount(2);
            scoringOnly.startTest(0);
            scoringOnly.setDebugBoard(SymbolType.ONE, null, null, null, null, null, null, null, null);
            scoringOnly.setState(GridTestState.WAITING_FOR_INPUT);
            assertFalse(scoringOnly.shouldFinishWhenStable());
            assertEquals(1, scoringOnly.performSuperReroll(new FixedReroller(SymbolType.FIVE)).size());
            assertEquals(1, scoringOnly.getSuperRerollsRemaining());
            assertFalse(scoringOnly.canUseSuperReroll());
            scoringOnly.setState(GridTestState.CHECKING_MATCHES);
            assertTrue(scoringOnly.shouldFinishWhenStable());

            GridTestController missingNext = createController(new QueueSymbolProvider() {
                @Override
                public SymbolType peekNext() {
                    return null;
                }
            });
            missingNext.setSelectedMode(mode);
            missingNext.setInitialSwapCount(0);
            missingNext.setInitialSuperRerollCount(0);
            missingNext.startTest(0);
            missingNext.setState(GridTestState.WAITING_FOR_INPUT);
            assertTrue(missingNext.getBoard().hasOccupiedCell());
            assertNull(missingNext.getNextToken());
            assertEquals(1, missingNext.getPickupsAvailable());
            assertFalse(missingNext.canUsePickup());
            assertTrue(missingNext.shouldFinishWhenStable());
            missingNext.setState(GridTestState.CHECKING_MATCHES);
            assertTrue(missingNext.shouldFinishWhenStable());
        }
    }

    @Test
    public void availableShiftsPreventCompletionEvenWithoutTacticalCharges() {
        GridTestController controller = createController(new QueueSymbolProvider());
        controller.setInitialSwapCount(0);
        controller.setInitialSuperRerollCount(0);
        controller.setInitialPickupCount(0);
        controller.startTest(1);
        controller.setState(GridTestState.CHECKING_MATCHES);
        assertFalse(controller.shouldFinishWhenStable());
    }

    @Test
    public void bonusShiftsCountAsActualUsesForNormalAndBlindMovesInEveryMode() {
        for (TestMode mode : TestMode.values()) {
            for (boolean momentum : new boolean[] {false, true}) {
                for (boolean blind : new boolean[] {false, true}) {
                    for (SymbolType symbol : new SymbolType[] {SymbolType.ONE, SymbolType.FIVE}) {
                        GridTestController controller = createController(new QueueSymbolProvider(
                                SymbolType.ONE, SymbolType.TWO, SymbolType.THREE,
                                SymbolType.FOUR, SymbolType.FIVE, SymbolType.SIX,
                                SymbolType.TWO, SymbolType.THREE, SymbolType.FOUR,
                                symbol, SymbolType.ONE, SymbolType.TWO, SymbolType.THREE, SymbolType.SIX
                        ));
                        controller.setSelectedMode(mode);
                        controller.setConfiguredMomentum(momentum);
                        controller.setConfiguredBlindEnabled(blind);
                        controller.startTest(1);
                        int row;
                        if (mode == TestMode.CURSED) {
                            controller.setDebugBoard(symbol, null, null, null, symbol, null, null, null, null);
                            row = 2;
                        } else {
                            controller.setDebugBoard(null, symbol, symbol, null, null, null, null, null, null);
                            row = 0;
                        }
                        controller.setState(GridTestState.WAITING_FOR_INPUT);
                        applyShift(controller, blind, new GridMove(GridMoveType.ROW_LEFT, row));
                        assertEquals(0, controller.getMovesRemaining());
                        MatchResolution resolution = controller.resolveMatches(controller.findMatches());
                        assertEquals(1, resolution.getMatchedLines());
                        assertEquals(symbol == SymbolType.FIVE ? 1 : 0, resolution.getSuccessesGained());
                        boolean bonus = momentum || symbol == SymbolType.ONE;
                        assertEquals(bonus ? 1 : 0, controller.getMovesRemaining());

                        if (bonus) {
                            controller.setState(GridTestState.WAITING_FOR_INPUT);
                            applyShift(controller, blind, new GridMove(GridMoveType.ROW_LEFT, 1));
                            assertEquals(0, controller.getMovesRemaining());
                        }
                        GridTestResult result = controller.finish();
                        assertEquals(bonus ? 2 : 1, result.getMovesUsed());
                        assertEquals(bonus ? 2 : 1, controller.finish().getMovesUsed());
                        controller.startTest(7);
                        assertEquals(0, controller.finish().getMovesUsed());
                    }
                }
            }
        }
    }

    @Test
    public void invalidShiftDoesNotSpendMoveOrTokenOrChangeState() {
        CountingQueueProvider provider = new CountingQueueProvider();
        GridTestController controller = createController(provider);
        controller.startTest(1);
        controller.setState(GridTestState.WAITING_FOR_INPUT);
        SymbolType next = controller.getNextToken();
        int consumed = provider.consumed;
        SymbolType firstCell = controller.getBoard().getCell(0, 0);

        try {
            controller.applyMove(null);
            fail("Null shift must be rejected before spending");
        } catch (IllegalArgumentException expected) {
            assertEquals(1, controller.getMovesRemaining());
            assertEquals(GridTestState.WAITING_FOR_INPUT, controller.getState());
            assertEquals(next, controller.getNextToken());
            assertEquals(consumed, provider.consumed);
            assertEquals(firstCell, controller.getBoard().getCell(0, 0));
            assertEquals(0, controller.finish().getMovesUsed());
        }
    }

    private static void applyShift(GridTestController controller, boolean blind, GridMove move) {
        if (blind) {
            controller.commitBlindMove(move);
            controller.applyCommittedBlindMove();
        } else {
            controller.applyMove(move);
        }
    }

    private static class FixedReroller extends SymbolReroller {
        private final SymbolType result;

        FixedReroller(SymbolType result) {
            super(new Random(0L));
            this.result = result;
        }

        @Override
        public SymbolType reroll(SymbolType previousSymbol) {
            return result;
        }
    }

    @Test
    public void selectedModeDoesNotChangeActiveModeUntilRestart() {
        GridTestController controller = createController(new QueueSymbolProvider(
                SymbolType.ONE, SymbolType.TWO, SymbolType.THREE,
                SymbolType.TWO, SymbolType.FOUR, SymbolType.ONE,
                SymbolType.THREE, SymbolType.ONE, SymbolType.TWO,
                SymbolType.ONE, SymbolType.ONE, SymbolType.ONE,
                SymbolType.TWO, SymbolType.THREE, SymbolType.FOUR,
                SymbolType.THREE, SymbolType.FOUR, SymbolType.TWO
        ));
        controller.startTest(3);
        assertEquals(TestMode.NORMAL, controller.getActiveMode());

        controller.setSelectedMode(TestMode.CURSED);
        assertEquals(TestMode.CURSED, controller.getSelectedMode());
        assertEquals(TestMode.NORMAL, controller.getActiveMode());

        controller.startTest(3);
        assertEquals(TestMode.CURSED, controller.getActiveMode());
        assertTrue(controller.findMatches().isEmpty());
        assertEquals(1, controller.getBoard().findMatches(TestMode.NORMAL).size());
    }

    @Test
    public void cascadesUseActiveMode() {
        GridTestController controller = createController(new QueueSymbolProvider(
                SymbolType.ONE, SymbolType.TWO, SymbolType.THREE,
                SymbolType.TWO, SymbolType.FOUR, SymbolType.ONE,
                SymbolType.THREE, SymbolType.ONE, SymbolType.TWO,
                SymbolType.FIVE, SymbolType.FIVE, SymbolType.FIVE
        ));
        controller.setSelectedMode(TestMode.CURSED);
        controller.startTest(3);
        controller.setDebugBoard(
                SymbolType.ONE, SymbolType.TWO, SymbolType.THREE,
                SymbolType.TWO, SymbolType.ONE, SymbolType.FOUR,
                SymbolType.THREE, SymbolType.FOUR, SymbolType.ONE
        );

        MatchResolution firstWave = controller.resolveMatches(controller.findMatches());
        assertEquals(1, firstWave.getMatchedLines());
        List<GridMatch> cascadeMatches = controller.findMatches();
        assertEquals(1, cascadeMatches.size());
        assertEquals(SymbolType.FIVE, cascadeMatches.get(0).getSymbol());
    }

    @Test
    public void fourFourFourStillAwardsBonusMove() {
        GridTestController controller = createController(new QueueSymbolProvider(
                SymbolType.ONE, SymbolType.TWO, SymbolType.THREE,
                SymbolType.TWO, SymbolType.FOUR, SymbolType.ONE,
                SymbolType.THREE, SymbolType.ONE, SymbolType.TWO,
                SymbolType.FOUR,
                SymbolType.ONE, SymbolType.TWO, SymbolType.THREE
        ));
        controller.startTest(1);
        controller.setDebugBoard(
                SymbolType.TWO, SymbolType.FOUR, SymbolType.FOUR,
                SymbolType.ONE, SymbolType.THREE, SymbolType.ONE,
                SymbolType.ONE, SymbolType.TWO, SymbolType.THREE
        );
        controller.setState(GridTestState.WAITING_FOR_INPUT);
        controller.applyMove(new GridMove(GridMoveType.ROW_LEFT, 0));
        controller.resolveMatches(controller.findMatches());
        assertEquals(1, controller.getMovesRemaining());
    }

    @Test
    public void configuredGapsAreAppliedWhenTestStarts() {
        GridTestController controller = createController(new QueueSymbolProvider(
                SymbolType.ONE, SymbolType.TWO, SymbolType.THREE,
                SymbolType.TWO, SymbolType.THREE, SymbolType.FOUR,
                SymbolType.THREE, SymbolType.FOUR, SymbolType.FIVE
        ));
        controller.setConfiguredGapCount(3);

        controller.startTest(3);

        assertEquals(3, controller.getGapCount());
    }

    private GridTestController createController(SymbolRandomProvider provider) {
        return new GridTestController(new GridBoard(provider));
    }

    private void resolveAllMatches(GridTestController controller) {
        int safety = 0;
        while (true) {
            List<GridMatch> matches = controller.findMatches();
            if (matches.isEmpty()) {
                return;
            }
            controller.resolveMatches(matches);
            safety++;
            if (safety > 20) {
                throw new IllegalStateException("Unexpected infinite cascade");
            }
        }
    }

    static class QueueSymbolProvider implements SymbolRandomProvider {
        private final Deque<SymbolType> queue = new ArrayDeque<>();
        private final SymbolType[] fallback = new SymbolType[] {
                SymbolType.ONE, SymbolType.TWO, SymbolType.THREE, SymbolType.FIVE, SymbolType.SIX
        };
        private int fallbackIndex;

        QueueSymbolProvider(SymbolType... symbols) {
            queue.addAll(Arrays.asList(symbols));
        }

        @Override
        public SymbolType peekNext() {
            if (!queue.isEmpty()) {
                return queue.peekFirst();
            }
            return fallback[fallbackIndex % fallback.length];
        }

        @Override
        public SymbolType next() {
            if (queue.isEmpty()) {
                SymbolType symbol = fallback[fallbackIndex % fallback.length];
                fallbackIndex++;
                return symbol;
            }
            return queue.removeFirst();
        }
    }

    static class CountingQueueProvider extends QueueSymbolProvider {
        int consumed;

        CountingQueueProvider(SymbolType... symbols) {
            super(symbols);
        }

        @Override
        public SymbolType next() {
            consumed++;
            return super.next();
        }
    }
}
