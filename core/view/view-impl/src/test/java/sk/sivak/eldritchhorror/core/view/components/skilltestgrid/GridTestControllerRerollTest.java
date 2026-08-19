package sk.sivak.eldritchhorror.core.view.components.skilltestgrid;

import org.junit.Test;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

public class GridTestControllerRerollTest {

    @Test
    public void startAndRestartInitializeConfiguredRerolls() {
        GridTestController controller = createController(new QueueProvider((Object) stableBoard(), (Object) stableBoard()));
        controller.setStartingRerolls(2);
        controller.startTest(5);
        makeReady(controller);

        reroll(controller, new GridPosition(0, 0), SymbolType.SIX);
        assertEquals(1, controller.getRemainingRerolls());

        controller.startTest(5);
        assertEquals(2, controller.getStartingRerolls());
        assertEquals(2, controller.getRemainingRerolls());
    }

    @Test
    public void selectedCenterAndOuterTokensChangeToDifferentSymbols() {
        GridTestController centerController = createReadyController(new QueueProvider((Object) stableBoard()));
        SymbolType centerOriginal = centerController.getBoard().getCell(1, 1);
        reroll(centerController, new GridPosition(1, 1), SymbolType.SIX);
        assertNotEquals(centerOriginal, centerController.getBoard().getCell(1, 1));

        GridTestController outerController = createReadyController(new QueueProvider((Object) stableBoard()));
        SymbolType outerOriginal = outerController.getBoard().getCell(0, 0);
        reroll(outerController, new GridPosition(0, 0), SymbolType.SIX);
        assertNotEquals(outerOriginal, outerController.getBoard().getCell(0, 0));
    }

    @Test
    public void rerollConsumesExactlyOneResourceAndNoMoves() {
        GridTestController controller = createReadyController(new QueueProvider((Object) stableBoard()));
        int movesBefore = controller.getMovesRemaining();

        reroll(controller, new GridPosition(2, 2), SymbolType.SIX);

        assertEquals(0, controller.getRemainingRerolls());
        assertEquals(movesBefore, controller.getMovesRemaining());
    }

    @Test
    public void nextTokenRemainsUnchanged() {
        QueueProvider provider = new QueueProvider((Object) stableBoard(), SymbolType.FIVE);
        GridTestController controller = createReadyController(provider);
        SymbolType nextBefore = provider.peekNext();

        reroll(controller, new GridPosition(0, 0), SymbolType.SIX);

        assertEquals(nextBefore, provider.peekNext());
        assertEquals(9, provider.getConsumedCount());
    }

    @Test
    public void blindStaysActiveAndNextTokenRemainsUnchanged() {
        QueueProvider provider = new QueueProvider((Object) stableBoard(), SymbolType.FIVE);
        GridTestController controller = createController(provider);
        controller.setConfiguredBlindEnabled(true);
        controller.startTest(5);
        makeReady(controller);
        SymbolType hiddenNext = provider.peekNext();

        assertTrue(controller.canActivateReroll());
        reroll(controller, new GridPosition(1, 1), SymbolType.SIX);

        assertTrue(controller.isBlindEnabled());
        assertEquals(hiddenNext, provider.peekNext());
        assertEquals(9, provider.getConsumedCount());
    }

    @Test
    public void rerollCreatedMatchResolvesImmediatelyWithNormalRewards() {
        QueueProvider provider = new QueueProvider(
                stableBoard(),
                SymbolType.TWO, SymbolType.THREE, SymbolType.FOUR
        );
        GridTestController controller = createReadyController(provider);
        controller.setDebugBoard(
                SymbolType.ONE, SymbolType.ONE, SymbolType.TWO,
                SymbolType.TWO, SymbolType.THREE, SymbolType.FOUR,
                SymbolType.THREE, SymbolType.FOUR, SymbolType.ONE
        );

        reroll(controller, new GridPosition(0, 2), SymbolType.ONE);
        MatchResolution resolution = controller.resolveMatches(controller.findMatches());

        assertEquals(1, resolution.getMatchedLines());
        assertEquals(6, controller.getMovesRemaining());
        assertEquals(0, controller.getSuccesses());
    }

    @Test
    public void momentumRewardsApplyToRerollCreatedScoringMatch() {
        GridTestController controller = createController(new QueueProvider((Object) stableBoard()));
        controller.setConfiguredMomentum(true);
        controller.startTest(5);
        controller.setDebugBoard(
                SymbolType.FIVE, SymbolType.FIVE, SymbolType.TWO,
                SymbolType.TWO, SymbolType.THREE, SymbolType.FOUR,
                SymbolType.THREE, SymbolType.FOUR, SymbolType.ONE
        );
        makeReady(controller);

        reroll(controller, new GridPosition(0, 2), SymbolType.FIVE);
        controller.resolveMatches(controller.findMatches());

        assertEquals(1, controller.getSuccesses());
        assertEquals(6, controller.getMovesRemaining());
    }

    @Test
    public void activeModeControlsRerollCreatedMatches() {
        for (TestMode mode : TestMode.values()) {
            GridTestController controller = createController(new QueueProvider((Object) stableBoard()));
            controller.setSelectedMode(mode);
            controller.startTest(5);
            controller.setDebugBoard(
                    SymbolType.FIVE, SymbolType.ONE, SymbolType.TWO,
                    SymbolType.THREE, SymbolType.FIVE, SymbolType.FOUR,
                    SymbolType.TWO, SymbolType.THREE, SymbolType.ONE
            );
            makeReady(controller);

            reroll(controller, new GridPosition(2, 2), SymbolType.FIVE);

            assertEquals(mode == TestMode.NORMAL ? 0 : 1, controller.findMatches().size());
        }
    }

    @Test
    public void rerollResolvesValidMatchesInBlessedNormalAndCursedModes() {
        for (TestMode mode : TestMode.values()) {
            GridTestController controller = createController(new QueueProvider((Object) stableBoard()));
            controller.setSelectedMode(mode);
            controller.startTest(5);
            if (mode == TestMode.CURSED) {
                controller.setDebugBoard(
                        SymbolType.FIVE, SymbolType.ONE, SymbolType.TWO,
                        SymbolType.THREE, SymbolType.FIVE, SymbolType.FOUR,
                        SymbolType.TWO, SymbolType.THREE, SymbolType.ONE
                );
                makeReady(controller);
                reroll(controller, new GridPosition(2, 2), SymbolType.FIVE);
            } else {
                controller.setDebugBoard(
                        SymbolType.FIVE, SymbolType.FIVE, SymbolType.TWO,
                        SymbolType.TWO, SymbolType.THREE, SymbolType.FOUR,
                        SymbolType.THREE, SymbolType.FOUR, SymbolType.ONE
                );
                makeReady(controller);
                reroll(controller, new GridPosition(0, 2), SymbolType.FIVE);
            }

            controller.resolveMatches(controller.findMatches());
            assertEquals(1, controller.getSuccesses());
        }
    }

    @Test
    public void cascadesResolveNormallyAfterReroll() {
        QueueProvider provider = new QueueProvider(
                stableBoard(),
                SymbolType.FIVE, SymbolType.FIVE, SymbolType.FIVE,
                SymbolType.ONE, SymbolType.TWO, SymbolType.THREE
        );
        GridTestController controller = createReadyController(provider);
        controller.setDebugBoard(
                SymbolType.ONE, SymbolType.ONE, SymbolType.TWO,
                SymbolType.TWO, SymbolType.THREE, SymbolType.FOUR,
                SymbolType.THREE, SymbolType.FOUR, SymbolType.ONE
        );

        reroll(controller, new GridPosition(0, 2), SymbolType.ONE);
        controller.resolveMatches(controller.findMatches());
        controller.resolveMatches(controller.findMatches());

        assertEquals(1, controller.getSuccesses());
        assertEquals(6, controller.getMovesRemaining());
    }

    @Test
    public void cannotActivateWithNoRerollsOrWhileResolving() {
        GridTestController empty = createController(new QueueProvider((Object) stableBoard()));
        empty.setStartingRerolls(0);
        empty.startTest(5);
        makeReady(empty);
        assertFalse(empty.canActivateReroll());
        assertFalse(empty.beginRerollTargeting());

        GridTestController resolving = createReadyController(new QueueProvider((Object) stableBoard()));
        resolving.setState(GridTestState.MATCH_ANIMATION);
        assertFalse(resolving.canActivateReroll());
        assertFalse(resolving.beginRerollTargeting());
    }

    @Test
    public void cannotActivateWhileBoardContainsUnresolvedMatches() {
        GridTestController controller = createReadyController(new QueueProvider((Object) stableBoard()));
        controller.setDebugBoard(
                SymbolType.ONE, SymbolType.ONE, SymbolType.ONE,
                SymbolType.TWO, SymbolType.THREE, SymbolType.FOUR,
                SymbolType.THREE, SymbolType.FOUR, SymbolType.TWO
        );

        assertFalse(controller.canActivateReroll());
        assertFalse(controller.beginRerollTargeting());
        assertEquals(1, controller.getRemainingRerolls());
    }

    @Test
    public void cancellingTargetingConsumesNothingAndRestoresInputState() {
        GridTestController controller = createReadyController(new QueueProvider((Object) stableBoard()));
        int movesBefore = controller.getMovesRemaining();

        assertTrue(controller.beginRerollTargeting());
        assertEquals(GridTestState.REROLL_SELECTING, controller.getState());
        assertTrue(controller.cancelRerollTargeting());

        assertEquals(GridTestState.WAITING_FOR_INPUT, controller.getState());
        assertEquals(1, controller.getRemainingRerolls());
        assertEquals(movesBefore, controller.getMovesRemaining());
    }

    @Test(expected = IllegalArgumentException.class)
    public void negativeStartingRerollsAreRejected() {
        createController(new QueueProvider()).setStartingRerolls(-1);
    }

    private static SymbolType reroll(GridTestController controller, GridPosition position, SymbolType result) {
        assertTrue(controller.beginRerollTargeting());
        return controller.performReroll(position, new FixedReroller(result));
    }

    private static GridTestController createReadyController(SymbolRandomProvider provider) {
        GridTestController controller = createController(provider);
        controller.startTest(5);
        makeReady(controller);
        return controller;
    }

    private static GridTestController createController(SymbolRandomProvider provider) {
        return new GridTestController(new GridBoard(provider));
    }

    private static void makeReady(GridTestController controller) {
        controller.setState(GridTestState.WAITING_FOR_INPUT);
    }

    private static SymbolType[] stableBoard() {
        return new SymbolType[] {
                SymbolType.ONE, SymbolType.TWO, SymbolType.THREE,
                SymbolType.FOUR, SymbolType.FIVE, SymbolType.SIX,
                SymbolType.TWO, SymbolType.THREE, SymbolType.FOUR
        };
    }

    private static class FixedReroller extends SymbolReroller {
        private final SymbolType result;

        FixedReroller(SymbolType result) {
            super(new Random(0L));
            this.result = result;
        }

        @Override
        public SymbolType reroll(SymbolType previousSymbol) {
            if (result == previousSymbol) {
                throw new IllegalArgumentException("Result must differ from previous symbol");
            }
            return result;
        }
    }

    private static class QueueProvider implements SymbolRandomProvider {
        private final Deque<SymbolType> symbols = new ArrayDeque<>();
        private final SymbolType[] fallback = stableBoard();
        private int fallbackIndex;
        private int consumedCount;

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
            SymbolType result = fallback[fallbackIndex % fallback.length];
            fallbackIndex++;
            return result;
        }

        int getConsumedCount() {
            return consumedCount;
        }
    }
}
