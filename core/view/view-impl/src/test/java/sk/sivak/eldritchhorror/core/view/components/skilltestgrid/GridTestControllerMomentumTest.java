package sk.sivak.eldritchhorror.core.view.components.skilltestgrid;

import org.junit.Test;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;
import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class GridTestControllerMomentumTest {

    @Test
    public void scoringMatchesAwardMomentumOnlyWhenActive() {
        GridTestController withoutMomentum = createStartedController(TestMode.NORMAL, false, 3);
        setHorizontalMatch(withoutMomentum, SymbolType.FIVE);
        withoutMomentum.resolveMatches(withoutMomentum.findMatches());
        assertEquals(1, withoutMomentum.getSuccesses());
        assertEquals(3, withoutMomentum.getMovesRemaining());

        GridTestController withMomentum = createStartedController(TestMode.NORMAL, true, 3);
        setHorizontalMatch(withMomentum, SymbolType.FIVE);
        withMomentum.resolveMatches(withMomentum.findMatches());
        assertEquals(1, withMomentum.getSuccesses());
        assertEquals(4, withMomentum.getMovesRemaining());

        GridTestController sixesWithMomentum = createStartedController(TestMode.NORMAL, true, 3);
        setHorizontalMatch(sixesWithMomentum, SymbolType.SIX);
        sixesWithMomentum.resolveMatches(sixesWithMomentum.findMatches());
        assertEquals(1, sixesWithMomentum.getSuccesses());
        assertEquals(4, sixesWithMomentum.getMovesRemaining());
    }

    @Test
    public void configuredMomentumDoesNotChangeActiveTestUntilRestart() {
        GridTestController controller = createStartedController(TestMode.NORMAL, false, 3);
        controller.setConfiguredMomentum(true);

        assertTrue(controller.isConfiguredMomentum());
        assertFalse(controller.isActiveMomentum());
        setHorizontalMatch(controller, SymbolType.FIVE);
        controller.resolveMatches(controller.findMatches());
        assertEquals(3, controller.getMovesRemaining());

        controller.startTest(3);
        assertTrue(controller.isActiveMomentum());
        setHorizontalMatch(controller, SymbolType.FIVE);
        controller.resolveMatches(controller.findMatches());
        assertEquals(4, controller.getMovesRemaining());
    }

    @Test
    public void neutralMatchKeepsExistingRewardWithMomentum() {
        QueueProvider provider = new QueueProvider(
                stableBoard(),
                SymbolType.THREE,
                SymbolType.ONE, SymbolType.TWO, SymbolType.FOUR
        );
        GridTestController controller = new GridTestController(new GridBoard(provider));
        controller.setConfiguredMomentum(true);
        controller.startTest(1);
        controller.setDebugBoard(
                SymbolType.TWO, SymbolType.THREE, SymbolType.THREE,
                SymbolType.ONE, SymbolType.FOUR, SymbolType.ONE,
                SymbolType.ONE, SymbolType.TWO, SymbolType.FOUR
        );
        controller.setState(GridTestState.WAITING_FOR_INPUT);

        controller.applyMove(new GridMove(GridMoveType.ROW_LEFT, 0));
        controller.resolveMatches(controller.findMatches());

        assertEquals(1, controller.getMovesRemaining());
        assertEquals(0, controller.getSuccesses());
    }

    @Test
    public void eachScoringLineAwardsMomentumIndependently() {
        GridTestController controller = createStartedController(TestMode.NORMAL, true, 3);
        controller.setDebugBoard(
                SymbolType.FIVE, SymbolType.FIVE, SymbolType.FIVE,
                SymbolType.SIX, SymbolType.SIX, SymbolType.SIX,
                SymbolType.ONE, SymbolType.TWO, SymbolType.THREE
        );

        controller.resolveMatches(controller.findMatches());

        assertEquals(2, controller.getSuccesses());
        assertEquals(5, controller.getMovesRemaining());
    }

    @Test
    public void swapCreatedScoringMatchAwardsMomentum() {
        GridTestController controller = createStartedController(TestMode.NORMAL, true, 3);
        controller.setDebugBoard(
                SymbolType.FIVE, SymbolType.FIVE, SymbolType.TWO,
                SymbolType.ONE, SymbolType.THREE, SymbolType.FIVE,
                SymbolType.THREE, SymbolType.FOUR, SymbolType.SIX
        );

        controller.performSwap(new GridPosition(0, 2), new GridPosition(1, 2));

        assertEquals(1, controller.getSuccesses());
        assertEquals(4, controller.getMovesRemaining());
    }

    @Test
    public void cascadeScoringMatchAwardsMomentum() {
        QueueProvider provider = new QueueProvider(
                stableBoard(),
                SymbolType.FIVE, SymbolType.FIVE, SymbolType.FIVE,
                SymbolType.ONE, SymbolType.TWO, SymbolType.THREE
        );
        GridTestController controller = new GridTestController(new GridBoard(provider));
        controller.setConfiguredMomentum(true);
        controller.startTest(3);
        controller.setDebugBoard(
                SymbolType.ONE, SymbolType.ONE, SymbolType.ONE,
                SymbolType.TWO, SymbolType.THREE, SymbolType.FOUR,
                SymbolType.THREE, SymbolType.FOUR, SymbolType.TWO
        );

        controller.resolveMatches(controller.findMatches());
        controller.resolveMatches(controller.findMatches());

        assertEquals(1, controller.getSuccesses());
        assertEquals(4, controller.getMovesRemaining());
    }

    @Test
    public void blessedAndCursedModesApplyMomentumOnlyToValidLines() {
        GridTestController blessed = createStartedController(TestMode.BLESSED, true, 3);
        setMainDiagonalMatch(blessed, SymbolType.FIVE);
        blessed.resolveMatches(blessed.findMatches());
        assertEquals(1, blessed.getSuccesses());
        assertEquals(4, blessed.getMovesRemaining());

        GridTestController cursedHorizontal = createStartedController(TestMode.CURSED, true, 3);
        setHorizontalMatch(cursedHorizontal, SymbolType.FIVE);
        assertTrue(cursedHorizontal.findMatches().isEmpty());
        cursedHorizontal.resolveMatches(cursedHorizontal.findMatches());
        assertEquals(0, cursedHorizontal.getSuccesses());
        assertEquals(3, cursedHorizontal.getMovesRemaining());

        GridTestController cursedDiagonal = createStartedController(TestMode.CURSED, true, 3);
        setMainDiagonalMatch(cursedDiagonal, SymbolType.SIX);
        cursedDiagonal.resolveMatches(cursedDiagonal.findMatches());
        assertEquals(1, cursedDiagonal.getSuccesses());
        assertEquals(4, cursedDiagonal.getMovesRemaining());
    }

    @Test
    public void momentumMovesAccumulateAcrossPlayerMoveAndCascade() {
        QueueProvider provider = new QueueProvider(
                stableBoard(),
                SymbolType.FIVE,
                SymbolType.SIX, SymbolType.SIX, SymbolType.SIX,
                SymbolType.ONE, SymbolType.TWO, SymbolType.THREE
        );
        GridTestController controller = new GridTestController(new GridBoard(provider));
        controller.setConfiguredMomentum(true);
        controller.startTest(1);
        controller.setDebugBoard(
                SymbolType.TWO, SymbolType.FIVE, SymbolType.FIVE,
                SymbolType.ONE, SymbolType.THREE, SymbolType.FOUR,
                SymbolType.THREE, SymbolType.FOUR, SymbolType.TWO
        );
        controller.setState(GridTestState.WAITING_FOR_INPUT);

        controller.applyMove(new GridMove(GridMoveType.ROW_LEFT, 0));
        controller.resolveMatches(controller.findMatches());
        assertEquals(1, controller.getMovesRemaining());

        controller.resolveMatches(controller.findMatches());
        assertEquals(2, controller.getMovesRemaining());
        assertEquals(2, controller.getSuccesses());

        controller.setState(GridTestState.WAITING_FOR_INPUT);
        controller.applyMove(new GridMove(GridMoveType.ROW_LEFT, 1));
        assertEquals(1, controller.getMovesRemaining());
    }

    @Test
    public void startBoardStillUsesSelectedModeWithMomentumEnabled() {
        for (TestMode mode : TestMode.values()) {
            GridTestController controller = createStartedController(mode, true, 3);
            assertEquals(mode, controller.getActiveMode());
            assertTrue(controller.findMatches().isEmpty());
        }
    }

    private static GridTestController createStartedController(TestMode mode, boolean momentum, int moves) {
        GridTestController controller = new GridTestController(new GridBoard(new RandomSymbolProvider(new Random(123L))));
        controller.setSelectedMode(mode);
        controller.setConfiguredMomentum(momentum);
        controller.startTest(moves);
        return controller;
    }

    private static void setHorizontalMatch(GridTestController controller, SymbolType symbol) {
        controller.setDebugBoard(
                symbol, symbol, symbol,
                SymbolType.ONE, SymbolType.TWO, SymbolType.THREE,
                SymbolType.TWO, SymbolType.THREE, SymbolType.FOUR
        );
    }

    private static void setMainDiagonalMatch(GridTestController controller, SymbolType symbol) {
        controller.setDebugBoard(
                symbol, SymbolType.ONE, SymbolType.TWO,
                SymbolType.THREE, symbol, SymbolType.FOUR,
                SymbolType.TWO, SymbolType.THREE, symbol
        );
    }

    private static SymbolType[] stableBoard() {
        return new SymbolType[] {
                SymbolType.ONE, SymbolType.TWO, SymbolType.THREE,
                SymbolType.TWO, SymbolType.THREE, SymbolType.FOUR,
                SymbolType.THREE, SymbolType.FOUR, SymbolType.ONE
        };
    }

    private static class QueueProvider implements SymbolRandomProvider {
        private final Deque<SymbolType> symbols = new ArrayDeque<>();

        QueueProvider(Object... values) {
            for (Object value : values) {
                if (value instanceof SymbolType[]) {
                    symbols.addAll(Arrays.asList((SymbolType[]) value));
                } else {
                    symbols.addLast((SymbolType) value);
                }
            }
        }

        @Override
        public SymbolType peekNext() {
            return symbols.isEmpty() ? SymbolType.ONE : symbols.peekFirst();
        }

        @Override
        public SymbolType next() {
            return symbols.isEmpty() ? SymbolType.ONE : symbols.removeFirst();
        }
    }
}
