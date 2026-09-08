package sk.sivak.eldritchhorror.core.view.components.skilltestgrid;

import org.junit.Test;

import java.util.Arrays;
import java.util.Random;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class GridTestParametersTest {
    @Test
    public void randomizedParametersAreDeterministicAndIncludeBothEndpoints() {
        Random random = new Random(20260908L);
        Random repeated = new Random(20260908L);
        int[] minimums = {1, 0, 0, 0, 0, 0};
        int[] maximums = {4, 4, 2, 1, 1, 3};
        int[] observedMinimums = new int[6];
        int[] observedMaximums = new int[6];
        Arrays.fill(observedMinimums, Integer.MAX_VALUE);
        for (int draw = 0; draw < 1000; draw++) {
            GridTestParameters parameters = GridTestParameters.randomized(random);
            int[] values = counts(parameters);
            assertArrayEquals(values, counts(GridTestParameters.randomized(repeated)));
            for (int index = 0; index < values.length; index++) {
                assertTrue(values[index] >= minimums[index]);
                assertTrue(values[index] <= maximums[index]);
                observedMinimums[index] = Math.min(observedMinimums[index], values[index]);
                observedMaximums[index] = Math.max(observedMaximums[index], values[index]);
            }
            assertFalse(parameters.isMomentum());
            assertFalse(parameters.isBlind());
            assertEquals(TestMode.NORMAL, parameters.getDifficulty());
            assertEquals(GridSuccessTarget.ONE, parameters.getSuccessTarget());
        }
        assertArrayEquals(minimums, observedMinimums);
        assertArrayEquals(maximums, observedMaximums);
    }

    @Test
    public void allOutOfRangeCountsAreRejectedRatherThanClamped() {
        int[] minimums = {1, 0, 0, 0, 0, 0};
        int[] maximums = {4, 4, 2, 1, 1, 3};
        for (int index = 0; index < minimums.length; index++) {
            int[] below = minimums.clone();
            below[index]--;
            expectIllegalArgument(() -> parameters(below));
            int[] above = maximums.clone();
            above[index]++;
            expectIllegalArgument(() -> parameters(above));
        }
    }

    @Test
    public void nullInputsAreExplicitlyRejected() {
        expectIllegalArgument(() -> new GridTestParameters(
                1, 0, 0, 0, 0, 0, false, false, null, GridSuccessTarget.ONE));
        expectIllegalArgument(() -> new GridTestParameters(
                1, 0, 0, 0, 0, 0, false, false, TestMode.NORMAL, null));
        expectIllegalArgument(() -> GridTestParameters.randomized(null));
        GridTestController controller = controller();
        expectIllegalArgument(() -> controller.startTest((GridTestParameters) null));
    }

    @Test
    public void startingAppliesAllCountsOptionsAndBoardGaps() {
        GridTestController controller = controller();
        Random random = new Random(49);
        for (TestMode mode : TestMode.values()) {
            for (int draw = 0; draw < 100; draw++) {
                GridTestParameters generated = GridTestParameters.randomized(random);
                GridTestParameters parameters = new GridTestParameters(
                        generated.getShifts(), generated.getSwaps(), generated.getRerolls(),
                        generated.getLifts(), generated.getSuperRerolls(), generated.getGaps(),
                        true, true, mode, GridSuccessTarget.TWO);
                controller.startTest(parameters);
                assertApplied(controller, parameters);
                assertEquals(parameters.getSwaps(), controller.getInitialSwapCount());
                assertEquals(parameters.getRerolls(), controller.getStartingRerolls());
                assertEquals(parameters.getLifts(), controller.getInitialPickupCount());
                assertEquals(parameters.getSuperRerolls(), controller.getInitialSuperRerollCount());
                assertEquals(parameters.getGaps(), controller.getConfiguredGapCount());
                assertEquals(mode, controller.getSelectedMode());
                assertTrue(controller.isConfiguredMomentum());
                assertTrue(controller.isConfiguredBlindEnabled());
                assertTrue(controller.findMatches().isEmpty());
                int actualGaps = 0;
                for (int row = 0; row < GridBoard.SIZE; row++) {
                    for (int column = 0; column < GridBoard.SIZE; column++) {
                        if (controller.getBoard().getCell(row, column) == null) {
                            actualGaps++;
                        }
                    }
                }
                assertEquals(parameters.getGaps(), actualGaps);
                assertEquals(GridTestState.INITIALIZING, controller.getState());
                assertEquals(0, controller.getSuccesses());
            }
        }
    }

    @Test
    public void activeSnapshotIsUnaffectedBySubsequentConfigurationChanges() {
        GridTestParameters parameters = new GridTestParameters(
                4, 4, 2, 1, 1, 3, true, true, TestMode.BLESSED, GridSuccessTarget.TWO);
        GridTestController controller = controller();
        controller.startTest(parameters);
        controller.setInitialSwapCount(0);
        controller.setStartingRerolls(0);
        controller.setInitialPickupCount(0);
        controller.setInitialSuperRerollCount(0);
        controller.setConfiguredGapCount(0);
        controller.setConfiguredMomentum(false);
        controller.setConfiguredBlindEnabled(false);
        controller.setSelectedMode(TestMode.CURSED);

        assertApplied(controller, parameters);
        assertArrayEquals(new int[]{4, 4, 2, 1, 1, 3}, counts(parameters));
        assertTrue(parameters.isMomentum());
        assertTrue(parameters.isBlind());
        assertEquals(TestMode.BLESSED, parameters.getDifficulty());
        assertEquals(GridSuccessTarget.TWO, parameters.getSuccessTarget());
    }

    @Test
    public void restartingWithParametersReplacesAllPreviousOptionsAndResources() {
        GridTestController controller = controller();
        controller.startTest(new GridTestParameters(
                4, 4, 2, 1, 1, 3, true, true, TestMode.BLESSED, GridSuccessTarget.TWO));
        controller.finish();
        GridTestParameters next = new GridTestParameters(
                1, 0, 0, 0, 0, 0, false, false, TestMode.NORMAL, GridSuccessTarget.ONE);
        controller.startTest(next);
        assertApplied(controller, next);
        assertEquals(GridTestState.INITIALIZING, controller.getState());
        assertEquals(0, controller.getSuccesses());
        assertEquals(0, controller.finish().getMovesUsed());
    }

    private static GridTestController controller() {
        return new GridTestController(new GridBoard(new RandomSymbolProvider(new Random(82)), new Random(17)));
    }

    private static int[] counts(GridTestParameters parameters) {
        return new int[]{parameters.getShifts(), parameters.getSwaps(), parameters.getRerolls(),
                parameters.getLifts(), parameters.getSuperRerolls(), parameters.getGaps()};
    }

    private static GridTestParameters parameters(int[] counts) {
        return new GridTestParameters(counts[0], counts[1], counts[2], counts[3], counts[4], counts[5],
                false, false, TestMode.NORMAL, GridSuccessTarget.ONE);
    }

    private static void assertApplied(GridTestController controller, GridTestParameters parameters) {
        assertEquals(parameters.getShifts(), controller.getMovesRemaining());
        assertEquals(parameters.getSwaps(), controller.getSwapRemaining());
        assertEquals(parameters.getRerolls(), controller.getRemainingRerolls());
        assertEquals(parameters.getLifts(), controller.getPickupsAvailable());
        assertEquals(parameters.getSuperRerolls(), controller.getSuperRerollsRemaining());
        assertEquals(parameters.getGaps(), controller.getGapCount());
        assertEquals(parameters.isMomentum(), controller.isActiveMomentum());
        assertEquals(parameters.isBlind(), controller.isBlindEnabled());
        assertEquals(parameters.getDifficulty(), controller.getActiveMode());
        assertEquals(parameters.getSuccessTarget(), controller.getSuccessTarget());
    }

    private static void expectIllegalArgument(Runnable action) {
        try {
            action.run();
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            assertTrue(expected.getMessage() != null && !expected.getMessage().isEmpty());
        }
    }
}
