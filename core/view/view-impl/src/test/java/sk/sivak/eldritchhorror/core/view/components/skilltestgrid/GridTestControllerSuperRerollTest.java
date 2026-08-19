package sk.sivak.eldritchhorror.core.view.components.skilltestgrid;

import org.junit.Test;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

public class GridTestControllerSuperRerollTest {
    @Test
    public void startInitializesSuperRerollToDefaultCount() {
        GridTestController controller = createController(new QueueProvider((Object) stableBoard()));

        controller.startTest(10);

        assertEquals(controller.getInitialSuperRerollCount(), controller.getSuperRerollsRemaining());
    }

    @Test
    public void restartRestoresSuperRerollCount() {
        GridTestController controller = createReadyController(new QueueProvider((Object) stableBoard(), (Object) stableBoard()));
        controller.setInitialSuperRerollCount(1);
        controller.setDebugBoard(
                SymbolType.ONE, SymbolType.FIVE, SymbolType.SIX,
                SymbolType.FIVE, SymbolType.SIX, SymbolType.FIVE,
                SymbolType.SIX, SymbolType.FIVE, SymbolType.SIX
        );

        controller.performSuperReroll(new SequenceReroller(SymbolType.TWO));
        assertEquals(0, controller.getSuperRerollsRemaining());

        controller.startTest(10);

        assertEquals(1, controller.getSuperRerollsRemaining());
    }

    @Test
    public void activationConsumesOneChargeAndNoMoves() {
        GridTestController controller = createReadyController(new QueueProvider((Object) stableBoard()));
        controller.setDebugBoard(
                SymbolType.ONE, SymbolType.FIVE, SymbolType.SIX,
                SymbolType.FIVE, SymbolType.SIX, SymbolType.FIVE,
                SymbolType.SIX, SymbolType.FIVE, SymbolType.SIX
        );

        controller.performSuperReroll(new SequenceReroller(SymbolType.TWO));

        assertEquals(0, controller.getSuperRerollsRemaining());
        assertEquals(10, controller.getMovesRemaining());
    }

    @Test
    public void valuesOneToFourAreRerolledAndAlwaysChange() {
        GridTestController controller = createReadyController(new QueueProvider((Object) stableBoard()));
        controller.setDebugBoard(
                SymbolType.ONE, SymbolType.TWO, SymbolType.THREE,
                SymbolType.FOUR, SymbolType.FIVE, SymbolType.SIX,
                SymbolType.ONE, SymbolType.TWO, SymbolType.THREE
        );

        Map<GridPosition, SymbolType> rerolled = controller.performSuperReroll(new SequenceReroller(
                SymbolType.SIX, SymbolType.ONE, SymbolType.SIX,
                SymbolType.TWO, SymbolType.FIVE, SymbolType.FOUR,
                SymbolType.ONE
        ));

        assertEquals(7, rerolled.size());
        assertNotEquals(SymbolType.ONE, controller.getBoard().getCell(0, 0));
        assertNotEquals(SymbolType.TWO, controller.getBoard().getCell(0, 1));
        assertNotEquals(SymbolType.THREE, controller.getBoard().getCell(0, 2));
        assertNotEquals(SymbolType.FOUR, controller.getBoard().getCell(1, 0));
        assertNotEquals(SymbolType.ONE, controller.getBoard().getCell(2, 0));
        assertNotEquals(SymbolType.TWO, controller.getBoard().getCell(2, 1));
        assertNotEquals(SymbolType.THREE, controller.getBoard().getCell(2, 2));
    }

    @Test
    public void fivesAndSixesRemainUntouched() {
        GridTestController controller = createReadyController(new QueueProvider((Object) stableBoard()));
        controller.setDebugBoard(
                SymbolType.ONE, SymbolType.FIVE, SymbolType.SIX,
                SymbolType.TWO, SymbolType.FIVE, SymbolType.SIX,
                SymbolType.THREE, SymbolType.FOUR, SymbolType.FIVE
        );

        controller.performSuperReroll(new SequenceReroller(
                SymbolType.TWO, SymbolType.THREE, SymbolType.ONE, SymbolType.SIX, SymbolType.ONE
        ));

        assertEquals(SymbolType.FIVE, controller.getBoard().getCell(0, 1));
        assertEquals(SymbolType.SIX, controller.getBoard().getCell(0, 2));
        assertEquals(SymbolType.FIVE, controller.getBoard().getCell(1, 1));
        assertEquals(SymbolType.SIX, controller.getBoard().getCell(1, 2));
        assertEquals(SymbolType.FIVE, controller.getBoard().getCell(2, 2));
    }

    @Test
    public void nextTokenRemainsUnchanged() {
        QueueProvider provider = new QueueProvider((Object) stableBoard(), SymbolType.FOUR);
        GridTestController controller = createReadyController(provider);
        controller.setDebugBoard(
                SymbolType.ONE, SymbolType.FIVE, SymbolType.SIX,
                SymbolType.TWO, SymbolType.FIVE, SymbolType.SIX,
                SymbolType.THREE, SymbolType.FOUR, SymbolType.FIVE
        );

        SymbolType nextBefore = provider.peekNext();
        controller.performSuperReroll(new SequenceReroller(
                SymbolType.TWO, SymbolType.THREE, SymbolType.ONE, SymbolType.SIX, SymbolType.ONE
        ));

        assertEquals(nextBefore, provider.peekNext());
    }

    @Test
    public void matchesCreatedBySuperRerollResolveAndAwardMoveRewards() {
        QueueProvider provider = new QueueProvider((Object) stableBoard(), (Object) stableBoard());
        GridTestController controller = createReadyController(provider);
        controller.setDebugBoard(
                SymbolType.ONE, SymbolType.ONE, SymbolType.ONE,
                SymbolType.FIVE, SymbolType.SIX, SymbolType.FIVE,
                SymbolType.SIX, SymbolType.FIVE, SymbolType.SIX
        );

        controller.performSuperReroll(new SequenceReroller(SymbolType.TWO, SymbolType.TWO, SymbolType.TWO));

        List<GridMatch> matches = controller.findMatches();
        assertEquals(1, matches.size());
        assertEquals(SymbolType.TWO, matches.get(0).getSymbol());

        MatchResolution resolution = controller.resolveMatches(matches);

        assertEquals(0, resolution.getSuccessesGained());
        assertEquals(11, controller.getMovesRemaining());
        assertEquals(0, controller.getSuccesses());
    }

    @Test
    public void scoringMatchesCreatedBySuperRerollAwardSuccesses() {
        GridTestController controller = createReadyController(new QueueProvider((Object) stableBoard(), (Object) stableBoard()));
        controller.setDebugBoard(
                SymbolType.ONE, SymbolType.ONE, SymbolType.ONE,
                SymbolType.FIVE, SymbolType.SIX, SymbolType.FIVE,
                SymbolType.SIX, SymbolType.FIVE, SymbolType.SIX
        );

        controller.performSuperReroll(new SequenceReroller(SymbolType.FIVE, SymbolType.FIVE, SymbolType.FIVE));
        MatchResolution resolution = controller.resolveMatches(controller.findMatches());

        assertEquals(1, resolution.getSuccessesGained());
        assertEquals(1, controller.getSuccesses());
        assertEquals(10, controller.getMovesRemaining());
    }

    @Test
    public void cascadesContinueNormallyAfterSuperReroll() {
        QueueProvider provider = new QueueProvider(
                stableBoard(),
                SymbolType.FIVE, SymbolType.FIVE, SymbolType.FIVE,
                SymbolType.ONE, SymbolType.TWO, SymbolType.THREE
        );
        GridTestController controller = createReadyController(provider);
        controller.setDebugBoard(
                SymbolType.ONE, SymbolType.ONE, SymbolType.ONE,
                SymbolType.FIVE, SymbolType.SIX, SymbolType.FIVE,
                SymbolType.SIX, SymbolType.FIVE, SymbolType.SIX
        );

        controller.performSuperReroll(new SequenceReroller(SymbolType.TWO, SymbolType.TWO, SymbolType.TWO));
        MatchResolution firstWave = controller.resolveMatches(controller.findMatches());
        MatchResolution secondWave = controller.resolveMatches(controller.findMatches());

        assertEquals(0, firstWave.getSuccessesGained());
        assertEquals(1, secondWave.getSuccessesGained());
        assertEquals(1, controller.getSuccesses());
        assertEquals(11, controller.getMovesRemaining());
    }

    @Test
    public void activeModeControlsWhichSuperRerollMatchesResolve() {
        GridTestController normal = createReadyControllerInMode(TestMode.NORMAL);
        normal.setDebugBoard(
                SymbolType.ONE, SymbolType.FIVE, SymbolType.SIX,
                SymbolType.FIVE, SymbolType.TWO, SymbolType.FIVE,
                SymbolType.SIX, SymbolType.FIVE, SymbolType.THREE
        );
        normal.performSuperReroll(new SequenceReroller(SymbolType.FOUR, SymbolType.FOUR, SymbolType.FOUR));
        assertTrue(normal.findMatches().isEmpty());

        GridTestController blessed = createReadyControllerInMode(TestMode.BLESSED);
        blessed.setDebugBoard(
                SymbolType.ONE, SymbolType.FIVE, SymbolType.SIX,
                SymbolType.FIVE, SymbolType.TWO, SymbolType.FIVE,
                SymbolType.SIX, SymbolType.FIVE, SymbolType.THREE
        );
        blessed.performSuperReroll(new SequenceReroller(SymbolType.FOUR, SymbolType.FOUR, SymbolType.FOUR));
        assertEquals(1, blessed.findMatches().size());

        GridTestController cursed = createReadyControllerInMode(TestMode.CURSED);
        cursed.setDebugBoard(
                SymbolType.ONE, SymbolType.FIVE, SymbolType.SIX,
                SymbolType.FIVE, SymbolType.TWO, SymbolType.FIVE,
                SymbolType.SIX, SymbolType.FIVE, SymbolType.THREE
        );
        cursed.performSuperReroll(new SequenceReroller(SymbolType.FOUR, SymbolType.FOUR, SymbolType.FOUR));
        assertEquals(1, cursed.findMatches().size());
    }

    @Test
    public void cannotActivateTwice() {
        GridTestController controller = createReadyController(new QueueProvider((Object) stableBoard()));
        controller.setDebugBoard(
                SymbolType.ONE, SymbolType.FIVE, SymbolType.SIX,
                SymbolType.FIVE, SymbolType.SIX, SymbolType.FIVE,
                SymbolType.SIX, SymbolType.FIVE, SymbolType.SIX
        );

        controller.performSuperReroll(new SequenceReroller(SymbolType.TWO));
        controller.setState(GridTestState.WAITING_FOR_INPUT);

        Map<GridPosition, SymbolType> secondAttempt = controller.performSuperReroll(new SequenceReroller(SymbolType.THREE));

        assertTrue(secondAttempt.isEmpty());
        assertEquals(0, controller.getSuperRerollsRemaining());
    }

    @Test
    public void cannotActivateWhileInputIsLocked() {
        GridTestController controller = createReadyController(new QueueProvider((Object) stableBoard()));
        controller.setDebugBoard(
                SymbolType.ONE, SymbolType.FIVE, SymbolType.SIX,
                SymbolType.FIVE, SymbolType.SIX, SymbolType.FIVE,
                SymbolType.SIX, SymbolType.FIVE, SymbolType.SIX
        );
        controller.setState(GridTestState.MATCH_ANIMATION);

        assertFalse(controller.canUseSuperReroll());
        try {
            controller.performSuperReroll(new SequenceReroller(SymbolType.TWO));
            throw new AssertionError("Expected IllegalStateException");
        } catch (IllegalStateException expected) {
            assertTrue(expected.getMessage().contains("Cannot use Super Reroll"));
        }
    }

    @Test
    public void noEligibleTokensLeavesChargeUnspent() {
        GridTestController controller = createReadyController(new QueueProvider((Object) stableBoard()));
        controller.setDebugBoard(
                SymbolType.FIVE, SymbolType.SIX, SymbolType.FIVE,
                SymbolType.SIX, SymbolType.FIVE, SymbolType.SIX,
                SymbolType.FIVE, SymbolType.SIX, SymbolType.FIVE
        );

        assertFalse(controller.canUseSuperReroll());
        Map<GridPosition, SymbolType> rerolled = controller.performSuperReroll(new SequenceReroller());

        assertTrue(rerolled.isEmpty());
        assertEquals(1, controller.getSuperRerollsRemaining());
    }

    @Test
    public void initialCountCanBeConfigured() {
        GridTestController controller = createController(new QueueProvider((Object) stableBoard()));
        controller.setInitialSuperRerollCount(2);

        controller.startTest(10);

        assertEquals(2, controller.getInitialSuperRerollCount());
        assertEquals(2, controller.getSuperRerollsRemaining());
    }

    private static GridTestController createController(SymbolRandomProvider provider) {
        return new GridTestController(new GridBoard(provider));
    }

    private static GridTestController createReadyController(SymbolRandomProvider provider) {
        GridTestController controller = createController(provider);
        controller.startTest(10);
        controller.setState(GridTestState.WAITING_FOR_INPUT);
        return controller;
    }

    private static GridTestController createReadyControllerInMode(TestMode mode) {
        GridTestController controller = createController(new QueueProvider((Object) stableBoard()));
        controller.setSelectedMode(mode);
        controller.startTest(10);
        controller.setState(GridTestState.WAITING_FOR_INPUT);
        return controller;
    }

    private static SymbolType[] stableBoard() {
        return new SymbolType[] {
                SymbolType.ONE, SymbolType.TWO, SymbolType.THREE,
                SymbolType.FOUR, SymbolType.FIVE, SymbolType.SIX,
                SymbolType.TWO, SymbolType.THREE, SymbolType.FOUR
        };
    }

    private static class QueueProvider implements SymbolRandomProvider {
        private final Deque<SymbolType> symbols = new ArrayDeque<>();

        QueueProvider(Object... values) {
            for (Object value : values) {
                if (value instanceof SymbolType[]) {
                    symbols.addAll(Arrays.asList((SymbolType[]) value));
                } else if (value instanceof SymbolType) {
                    symbols.addLast((SymbolType) value);
                } else if (value instanceof Iterable) {
                    for (Object symbol : (Iterable<?>) value) {
                        symbols.addLast((SymbolType) symbol);
                    }
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

    private static class SequenceReroller extends SymbolReroller {
        private final Deque<SymbolType> results = new ArrayDeque<>();

        SequenceReroller(SymbolType... symbols) {
            super(new Random(0L));
            results.addAll(Arrays.asList(symbols));
        }

        @Override
        public SymbolType reroll(SymbolType previousSymbol) {
            if (results.isEmpty()) {
                throw new IllegalStateException("No reroll values configured");
            }
            SymbolType rerolled = results.removeFirst();
            if (rerolled == previousSymbol) {
                throw new IllegalArgumentException("Reroll value must differ from previous symbol");
            }
            return rerolled;
        }
    }
}
