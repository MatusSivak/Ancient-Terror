package sk.sivak.eldritchhorror.core.view.components.skilltestgrid;

import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class GridTestOutcomeTest {
    @Test
    public void targetChoicesHaveExpectedThresholdsAndLabels() {
        assertEquals(1, GridSuccessTarget.ONE.getMinimumSuccesses());
        assertEquals(2, GridSuccessTarget.TWO.getMinimumSuccesses());
        assertEquals(0, GridSuccessTarget.UNLIMITED.getMinimumSuccesses());
        assertFalse(GridSuccessTarget.ONE.isUnlimited());
        assertFalse(GridSuccessTarget.TWO.isUnlimited());
        assertTrue(GridSuccessTarget.UNLIMITED.isUnlimited());
        assertEquals("1", GridSuccessTarget.ONE.toString());
        assertEquals("2", GridSuccessTarget.TWO.toString());
        assertEquals("Unlimited", GridSuccessTarget.UNLIMITED.toString());
    }

    @Test
    public void resultReportsSuccessOrFailureAtEachFiniteThreshold() {
        for (GridSuccessTarget target : new GridSuccessTarget[]{GridSuccessTarget.ONE, GridSuccessTarget.TWO}) {
            for (int successes = 0; successes <= 3; successes++) {
                GridTestResult result = new GridTestResult(successes, 4, target);
                assertEquals(successes, result.getSuccesses());
                assertEquals(4, result.getMovesUsed());
                assertEquals(target, result.getSuccessTarget());
                assertEquals(successes >= target.getMinimumSuccesses()
                        ? GridTestResult.Outcome.SUCCESS : GridTestResult.Outcome.FAILURE, result.getOutcome());
            }
        }
    }

    @Test
    public void unlimitedAndLegacyResultsAreAlwaysScoreOnly() {
        for (int successes : new int[]{0, 1, 2, Integer.MAX_VALUE}) {
            GridTestResult unlimited = new GridTestResult(successes, 8, GridSuccessTarget.UNLIMITED);
            GridTestResult legacy = new GridTestResult(successes, 8);
            assertEquals(GridTestResult.Outcome.SCORE_ONLY, unlimited.getOutcome());
            assertEquals(GridTestResult.Outcome.SCORE_ONLY, legacy.getOutcome());
            assertEquals(GridSuccessTarget.UNLIMITED, legacy.getSuccessTarget());
            assertEquals(successes, legacy.getSuccesses());
            assertEquals(8, legacy.getMovesUsed());
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullResultTargetIsRejected() {
        new GridTestResult(0, 0, null);
    }

    @Test
    public void exhaustionProducesTargetedOutcomeAndPreservesScoreAndMoves() {
        for (GridSuccessTarget target : GridSuccessTarget.values()) {
            for (int successes = 0; successes <= 2; successes++) {
                GridTestController controller = controller();
                controller.startTest(parameters(1, 0, 0, 0, 0, target));
                useShift(controller);
                awardSuccesses(controller, successes);
                assertTrue(controller.shouldFinishWhenStable());
                GridTestResult result = controller.finish();
                assertEquals(GridTestState.FINISHED, controller.getState());
                assertEquals(target, result.getSuccessTarget());
                assertEquals(successes, result.getSuccesses());
                assertEquals(1, result.getMovesUsed());
                assertEquals(new GridTestResult(successes, 1, target).getOutcome(), result.getOutcome());
            }
        }
    }

    @Test
    public void reachingOrExceedingTargetCompletesWithResourcesAndBlocksAllActions() {
        for (GridSuccessTarget target : new GridSuccessTarget[]{GridSuccessTarget.ONE, GridSuccessTarget.TWO}) {
            for (int successes = target.getMinimumSuccesses(); successes <= target.getMinimumSuccesses() + 1; successes++) {
                GridTestController controller = controller();
                controller.startTest(new GridTestParameters(4, 4, 2, 1, 1, 0,
                        true, true, TestMode.NORMAL, target));
                awardSuccesses(controller, successes);
                assertTrue(controller.hasReachedSuccessTarget());
                assertTrue(controller.shouldFinishWhenStable());
                controller.setState(GridTestState.WAITING_FOR_INPUT);
                assertFalse(controller.canAcceptInput());
                assertFalse(controller.beginSwapSelection(new GridPosition(0, 0)));
                assertFalse(controller.beginRerollTargeting());
                assertFalse(controller.startPickupMode());
                assertFalse(controller.canUseSuperReroll());
                assertRejected(() -> controller.applyMove(new GridMove(GridMoveType.ROW_LEFT, 0)));
                assertRejected(() -> controller.commitBlindMove(new GridMove(GridMoveType.ROW_LEFT, 0)));
                assertRejected(() -> controller.performSuperReroll(new SymbolReroller(new Random(1))));
                controller.setState(GridTestState.SWAP_SELECTING);
                assertRejected(() -> controller.performSwap(new GridPosition(0, 0), new GridPosition(0, 1)));
                controller.setState(GridTestState.REROLL_SELECTING);
                assertRejected(() -> controller.performReroll(new GridPosition(0, 0), new SymbolReroller(new Random(1))));
                controller.setState(GridTestState.PICKUP_SELECTING);
                assertRejected(() -> controller.pickupToken(0, 0));
                assertEquals(4 + successes, controller.getMovesRemaining());
                assertEquals(4, controller.getSwapRemaining());
                assertEquals(2, controller.getRemainingRerolls());
                assertEquals(1, controller.getPickupsAvailable());
                assertEquals(1, controller.getSuperRerollsRemaining());
                assertEquals(GridTestResult.Outcome.SUCCESS, controller.finish().getOutcome());
                assertEquals(GridTestState.FINISHED, controller.getState());
                assertEquals(0, controller.finish().getMovesUsed());
            }
        }
    }

    @Test
    public void belowTargetAndUnlimitedKeepPlayingWithAvailableActions() {
        for (GridSuccessTarget target : GridSuccessTarget.values()) {
            GridTestController controller = controller();
            controller.startTest(parameters(4, 1, 1, 1, 1, target));
            awardSuccesses(controller, target.isUnlimited() ? 5 : target.getMinimumSuccesses() - 1);
            assertFalse(controller.hasReachedSuccessTarget());
            assertFalse(controller.shouldFinishWhenStable());
            useShift(controller);
            controller.setState(GridTestState.WAITING_FOR_INPUT);
            assertTrue(controller.canAcceptInput());
            assertTrue(controller.beginSwapSelection(new GridPosition(0, 0)));
            controller.performSwap(new GridPosition(0, 0), new GridPosition(0, 1));
            assertEquals(0, controller.getSwapRemaining());
        }
    }

    @Test
    public void unusableRegularRerollsDoNotChangeExhaustionRules() {
        GridTestController controller = controller();
        controller.startTest(parameters(1, 0, 2, 0, 0, GridSuccessTarget.UNLIMITED));
        useShift(controller);
        controller.setState(GridTestState.WAITING_FOR_INPUT);
        assertEquals(2, controller.getRemainingRerolls());
        assertFalse(controller.canActivateReroll());
        assertTrue(controller.shouldFinishWhenStable());
        assertEquals(GridTestResult.Outcome.SCORE_ONLY, controller.finish().getOutcome());
    }

    @Test
    public void legacyRestartClearsTargetScoreAndMoveHistoryWithoutNewRangeLimits() {
        GridTestController controller = controller();
        controller.startTest(parameters(1, 0, 0, 0, 0, GridSuccessTarget.ONE));
        useShift(controller);
        awardSuccesses(controller, 1);
        GridTestResult previous = controller.finish();
        for (int shifts : new int[]{0, 5, Integer.MAX_VALUE}) {
            controller.startTest(shifts);
            assertEquals(shifts, controller.getMovesRemaining());
            assertEquals(GridSuccessTarget.UNLIMITED, controller.getSuccessTarget());
            assertEquals(0, controller.getSuccesses());
            GridTestResult result = controller.finish();
            assertEquals(0, result.getMovesUsed());
            assertEquals(GridTestResult.Outcome.SCORE_ONLY, result.getOutcome());
        }
        assertEquals(GridSuccessTarget.ONE, previous.getSuccessTarget());
        assertEquals(GridTestResult.Outcome.SUCCESS, previous.getOutcome());
        assertEquals(1, previous.getSuccesses());
        assertEquals(1, previous.getMovesUsed());
        controller.startTest(parameters(1, 0, 0, 0, 0, GridSuccessTarget.TWO));
        assertEquals(GridSuccessTarget.TWO, controller.getSuccessTarget());
        try {
            controller.startTest(-1);
            fail("Negative legacy shifts must be rejected");
        } catch (IllegalArgumentException expected) {
            assertEquals(GridSuccessTarget.TWO, controller.getSuccessTarget());
        }
    }

    private static GridTestController controller() {
        return new GridTestController(new GridBoard(new RandomSymbolProvider(new Random(93)), new Random(17)));
    }

    private static void assertRejected(Runnable action) {
        try {
            action.run();
            fail("Successful tests must reject further actions");
        } catch (IllegalStateException expected) {
            assertEquals("Test is already successful", expected.getMessage());
        }
    }

    private static GridTestParameters parameters(int shifts, int swaps, int rerolls, int lifts,
                                                 int superRerolls, GridSuccessTarget target) {
        return new GridTestParameters(shifts, swaps, rerolls, lifts, superRerolls, 0,
                false, false, TestMode.NORMAL, target);
    }

    private static void awardSuccesses(GridTestController controller, int successes) {
        for (int index = 0; index < successes; index++) {
            controller.setDebugBoard(
                    SymbolType.FIVE, SymbolType.FIVE, SymbolType.FIVE,
                    SymbolType.ONE, SymbolType.TWO, SymbolType.THREE,
                    SymbolType.TWO, SymbolType.THREE, SymbolType.FOUR);
            assertEquals(1, controller.resolveMatches(controller.findMatches()).getSuccessesGained());
        }
    }

    private static void useShift(GridTestController controller) {
        controller.setDebugBoard(
                SymbolType.ONE, SymbolType.TWO, SymbolType.THREE,
                SymbolType.TWO, SymbolType.THREE, SymbolType.FOUR,
                SymbolType.THREE, SymbolType.FOUR, SymbolType.FIVE);
        controller.setState(GridTestState.WAITING_FOR_INPUT);
        controller.applyMove(new GridMove(GridMoveType.ROW_LEFT, 0));
    }
}
