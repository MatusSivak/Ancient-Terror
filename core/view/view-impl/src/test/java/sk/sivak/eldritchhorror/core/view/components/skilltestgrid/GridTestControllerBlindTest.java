package sk.sivak.eldritchhorror.core.view.components.skilltestgrid;

import org.junit.Test;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class GridTestControllerBlindTest {

    @Test
    public void configuredBlindAppliesOnlyWhenTestStartsOrRestarts() {
        GridTestController controller = createController(new QueueProvider((Object) stableBoard()));
        controller.startTest(3);
        assertFalse(controller.isBlindEnabled());

        controller.setConfiguredBlindEnabled(true);
        assertTrue(controller.isConfiguredBlindEnabled());
        assertFalse(controller.isBlindEnabled());

        controller.startTest(3);
        assertTrue(controller.isBlindEnabled());

        controller.setConfiguredBlindEnabled(false);
        assertTrue(controller.isBlindEnabled());

        controller.startTest(3);
        assertFalse(controller.isBlindEnabled());
    }

    @Test
    public void blindAndMomentumCanBeActiveTogether() {
        GridTestController controller = createController(new QueueProvider((Object) stableBoard()));
        controller.setConfiguredBlindEnabled(true);
        controller.setConfiguredMomentum(true);

        controller.startTest(3);

        assertTrue(controller.isBlindEnabled());
        assertTrue(controller.isActiveMomentum());
    }

    @Test
    public void blindDoesNotChangeBoardOrNextTokenRandomness() {
        RandomSymbolProvider visibleProvider = new RandomSymbolProvider(new Random(9876L));
        GridTestController visible = createController(visibleProvider);
        RandomSymbolProvider blindProvider = new RandomSymbolProvider(new Random(9876L));
        GridTestController blind = createController(blindProvider);
        blind.setConfiguredBlindEnabled(true);

        visible.startTest(3);
        blind.startTest(3);

        for (int row = 0; row < GridBoard.SIZE; row++) {
            for (int column = 0; column < GridBoard.SIZE; column++) {
                assertEquals(
                        visible.getBoard().getCell(row, column),
                        blind.getBoard().getCell(row, column)
                );
            }
        }
        assertEquals(visibleProvider.peekNext(), blindProvider.peekNext());
    }

    @Test
    public void committingBlindMoveLocksDirectionWithoutConsumingMoveOrToken() {
        QueueProvider provider = new QueueProvider(
                stableBoard(),
                SymbolType.SIX
        );
        GridTestController controller = createController(provider);
        controller.setConfiguredBlindEnabled(true);
        controller.startTest(3);
        controller.setState(GridTestState.WAITING_FOR_INPUT);
        GridMove committed = new GridMove(GridMoveType.ROW_RIGHT, 1);
        SymbolType nextToken = provider.peekNext();
        int consumedBefore = provider.getConsumedCount();

        controller.commitBlindMove(committed);

        assertSame(committed, controller.getCommittedBlindMove());
        assertEquals(GridTestState.REVEALING_NEXT_TOKEN, controller.getState());
        assertEquals(3, controller.getMovesRemaining());
        assertEquals(consumedBefore, provider.getConsumedCount());
        assertEquals(nextToken, provider.peekNext());

        try {
            controller.commitBlindMove(new GridMove(GridMoveType.COLUMN_UP, 2));
            fail("Committed Blind movement must not be replaceable");
        } catch (IllegalStateException expected) {
            assertSame(committed, controller.getCommittedBlindMove());
        }
    }

    @Test
    public void committedShiftUsesPreviouslyGeneratedTokenAndNormalMoveCost() {
        QueueProvider provider = new QueueProvider(
                stableBoard(),
                SymbolType.SIX
        );
        GridTestController controller = createController(provider);
        controller.setConfiguredBlindEnabled(true);
        controller.startTest(3);
        controller.setDebugBoard(stableBoard());
        controller.setState(GridTestState.WAITING_FOR_INPUT);
        SymbolType generatedNext = provider.peekNext();

        controller.commitBlindMove(new GridMove(GridMoveType.ROW_RIGHT, 0));
        GridShiftOutcome outcome = controller.applyCommittedBlindMove();

        assertEquals(generatedNext, outcome.getIncomingSymbol());
        assertEquals(generatedNext, controller.getBoard().getCell(0, 0));
        assertEquals(2, controller.getMovesRemaining());
        assertEquals(GridTestState.SHIFTING, controller.getState());
    }

    @Test
    public void blindDoesNotChangeMomentumRewards() {
        GridTestController controller = createController(new QueueProvider((Object) stableBoard()));
        controller.setConfiguredBlindEnabled(true);
        controller.setConfiguredMomentum(true);
        controller.startTest(3);
        controller.setDebugBoard(
                SymbolType.FIVE, SymbolType.FIVE, SymbolType.FIVE,
                SymbolType.ONE, SymbolType.TWO, SymbolType.THREE,
                SymbolType.TWO, SymbolType.THREE, SymbolType.FOUR
        );

        controller.resolveMatches(controller.findMatches());

        assertEquals(1, controller.getSuccesses());
        assertEquals(4, controller.getMovesRemaining());
    }

    @Test
    public void blindWithMomentumRewardsNeutralMatches() {
        QueueProvider provider = new QueueProvider(
                (Object) stableBoard(),
                SymbolType.THREE,
                SymbolType.ONE, SymbolType.TWO, SymbolType.FOUR
        );
        GridTestController controller = createController(provider);
        controller.setConfiguredBlindEnabled(true);
        controller.setConfiguredMomentum(true);
        controller.startTest(1);
        controller.setDebugBoard(
                SymbolType.TWO, SymbolType.THREE, SymbolType.THREE,
                SymbolType.ONE, SymbolType.FOUR, SymbolType.ONE,
                SymbolType.ONE, SymbolType.TWO, SymbolType.FOUR
        );
        controller.setState(GridTestState.WAITING_FOR_INPUT);

        controller.commitBlindMove(new GridMove(GridMoveType.ROW_LEFT, 0));
        controller.applyCommittedBlindMove();
        controller.resolveMatches(controller.findMatches());

        assertEquals(1, controller.getMovesRemaining());
        assertEquals(0, controller.getSuccesses());
    }

    private static GridTestController createController(SymbolRandomProvider provider) {
        return new GridTestController(new GridBoard(provider));
    }

    private static SymbolType[] stableBoard() {
        return new SymbolType[]{
                SymbolType.ONE, SymbolType.TWO, SymbolType.THREE,
                SymbolType.TWO, SymbolType.THREE, SymbolType.FOUR,
                SymbolType.THREE, SymbolType.FOUR, SymbolType.ONE
        };
    }

    private static class QueueProvider implements SymbolRandomProvider {
        private final Deque<SymbolType> symbols = new ArrayDeque<>();
        private final SymbolType[] fallback = stableBoard();
        private int consumedCount;
        private int fallbackIndex;

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
            return symbols.isEmpty()
                    ? fallback[fallbackIndex % fallback.length]
                    : symbols.peekFirst();
        }

        @Override
        public SymbolType next() {
            consumedCount++;
            if (!symbols.isEmpty()) {
                return symbols.removeFirst();
            }
            SymbolType symbol = fallback[fallbackIndex % fallback.length];
            fallbackIndex++;
            return symbol;
        }

        int getConsumedCount() {
            return consumedCount;
        }
    }
}
