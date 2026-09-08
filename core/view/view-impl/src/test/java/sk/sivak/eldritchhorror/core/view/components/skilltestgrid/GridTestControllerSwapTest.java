package sk.sivak.eldritchhorror.core.view.components.skilltestgrid;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;

import static org.junit.Assert.*;
import static sk.sivak.eldritchhorror.core.view.components.skilltestgrid.SymbolType.*;

public class GridTestControllerSwapTest {
    private QueueProvider provider;
    private GridTestController controller;

    @Before
    public void setUp() {
        provider = new QueueProvider();
        controller = new GridTestController(new GridBoard(provider));
    }

    @Test
    public void tokenTapBeginsSelectionWithoutChangingBoardResourcesOrNextToken() {
        start(4);
        SymbolType next = controller.getNextToken();

        assertTrue(controller.beginSwapSelection(new GridPosition(0, 0)));

        assertEquals(GridTestState.SWAP_SELECTING, controller.getState());
        assertResources(3, 4);
        assertBoard(stableBoard());
        assertEquals(next, controller.getNextToken());
        assertEquals(9, provider.consumed);
    }

    @Test
    public void noSwapsOrGapCannotBeginSelection() {
        controller.setInitialSwapCount(0);
        start(4);
        assertFalse(controller.beginSwapSelection(new GridPosition(0, 0)));
        assertTrue(controller.canAcceptInput());

        controller.setInitialSwapCount(3);
        start(4);
        controller.setDebugBoard(null, TWO, THREE, FOUR, FIVE, SIX, TWO, THREE, FOUR);
        assertFalse(controller.beginSwapSelection(new GridPosition(0, 0)));
        assertEquals(GridTestState.WAITING_FOR_INPUT, controller.getState());
        assertResources(3, 4);
    }

    @Test
    public void tokenTapCannotInterruptOtherStates() {
        start(4);
        for (GridTestState state : GridTestState.values()) {
            if (state != GridTestState.WAITING_FOR_INPUT) {
                controller.setState(state);
                assertFalse(controller.beginSwapSelection(new GridPosition(0, 0)));
                assertEquals(state, controller.getState());
                assertResources(3, 4);
            }
        }
    }

    @Test
    public void noMatchHorizontalSwapAtomicallySpendsExactlyOneCharge() {
        start(4);
        SymbolType next = controller.getNextToken();
        assertTrue(controller.beginSwapSelection(new GridPosition(1, 0)));

        MatchResolution resolution = controller.performSwap(new GridPosition(1, 0), new GridPosition(1, 1));

        assertResolution(resolution, 0, 0);
        assertBoard(ONE, TWO, THREE, FIVE, FOUR, SIX, TWO, THREE, FOUR);
        assertResources(2, 4);
        assertEquals(GridTestState.CHECKING_MATCHES, controller.getState());
        assertEquals(next, controller.getNextToken());
        assertEquals(9, provider.consumed);
        assertEquals(0, controller.finish().getMovesUsed());
    }

    @Test
    public void verticalSwapAndTokenToGapSwapRemainValidWithZeroShifts() {
        start(0);
        assertTrue(controller.beginSwapSelection(new GridPosition(0, 0)));
        assertResolution(controller.performSwap(new GridPosition(0, 0), new GridPosition(1, 0)), 0, 0);
        assertBoard(FOUR, TWO, THREE, ONE, FIVE, SIX, TWO, THREE, FOUR);
        assertResources(2, 0);

        controller.setDebugBoard(FOUR, TWO, THREE, null, FIVE, SIX, TWO, THREE, FOUR);
        controller.setState(GridTestState.WAITING_FOR_INPUT);
        assertTrue(controller.beginSwapSelection(new GridPosition(0, 0)));
        assertResolution(controller.performSwap(new GridPosition(0, 0), new GridPosition(1, 0)), 0, 0);
        assertBoard(null, TWO, THREE, FOUR, FIVE, SIX, TWO, THREE, FOUR);
        assertResources(1, 0);
        assertEquals(1, controller.getGapCount());
        assertEquals(9, provider.consumed);
    }

    @Test
    public void invalidPairLeavesSelectionBoardResourcesAndReservedTokenIntact() {
        start(4);
        provider.add(SIX, ONE);
        controller.reserveNextToken();
        assertTrue(controller.beginSwapSelection(new GridPosition(0, 0)));
        GridPosition origin = new GridPosition(0, 0);
        GridPosition[][] invalidPairs = {
                {null, origin}, {origin, null}, {origin, origin},
                {origin, new GridPosition(1, 1)}, {origin, new GridPosition(0, 2)}
        };

        for (GridPosition[] pair : invalidPairs) {
            try {
                controller.performSwap(pair[0], pair[1]);
                fail("Invalid pair must be rejected");
            } catch (IllegalArgumentException expected) {
                assertEquals(GridTestState.SWAP_SELECTING, controller.getState());
                assertBoard(stableBoard());
                assertResources(3, 4);
                assertEquals(0, controller.getSuccesses());
                assertEquals(10, provider.consumed);
                assertEquals(ONE, controller.getNextToken());
            }
        }
        controller.releaseNextToken();
        assertEquals(SIX, controller.getNextToken());
        assertEquals(0, controller.finish().getMovesUsed());
    }

    @Test
    public void swapCannotExecuteOutsideSelectionOrSpendAnExhaustedCharge() {
        start(4);
        for (GridTestState state : GridTestState.values()) {
            if (state != GridTestState.SWAP_SELECTING) {
                controller.setState(state);
                assertSwapRejected();
                assertEquals(state, controller.getState());
                assertBoard(stableBoard());
                assertResources(3, 4);
            }
        }

        controller.setInitialSwapCount(1);
        start(4);
        assertTrue(controller.beginSwapSelection(new GridPosition(1, 0)));
        controller.performSwap(new GridPosition(1, 0), new GridPosition(1, 1));
        assertSwapRejected();
        controller.setState(GridTestState.WAITING_FOR_INPUT);
        assertFalse(controller.beginSwapSelection(new GridPosition(1, 0)));
        controller.setState(GridTestState.SWAP_SELECTING);
        assertSwapRejected();
        assertBoard(ONE, TWO, THREE, FIVE, FOUR, SIX, TWO, THREE, FOUR);
        assertResources(0, 4);
    }

    @Test
    public void cancellationRestoresAllInputPermissionsWithoutSpending() {
        start(4);
        assertTrue(controller.beginSwapSelection(new GridPosition(0, 0)));
        assertFalse(controller.canAcceptInput());
        assertFalse(controller.canActivateReroll());
        assertFalse(controller.canUseSuperReroll());
        assertFalse(controller.canUsePickup());

        assertTrue(controller.cancelSwapSelection());

        assertEquals(GridTestState.WAITING_FOR_INPUT, controller.getState());
        assertTrue(controller.canAcceptInput());
        assertTrue(controller.canActivateReroll());
        assertTrue(controller.canUseSuperReroll());
        assertTrue(controller.canUsePickup());
        assertBoard(stableBoard());
        assertResources(3, 4);
        assertEquals(ONE, controller.getNextToken());
        assertEquals(9, provider.consumed);
        assertTrue(controller.beginRerollTargeting());
        assertTrue(controller.cancelRerollTargeting());
        assertTrue(controller.startPickupMode());
        assertTrue(controller.cancelPickupMode());
        assertTrue(controller.beginSwapSelection(new GridPosition(0, 0)));
    }

    @Test
    public void cancellationDoesNotInterruptOtherStates() {
        start(4);
        for (GridTestState state : GridTestState.values()) {
            if (state != GridTestState.SWAP_SELECTING) {
                controller.setState(state);
                assertFalse(controller.cancelSwapSelection());
                assertEquals(state, controller.getState());
                assertResources(3, 4);
            }
        }
    }

    @Test
    public void swapRewardsAndReplacementCellsFollowEveryModeAndMomentumSetting() {
        for (TestMode mode : TestMode.values()) {
            for (boolean momentum : new boolean[] {false, true}) {
                for (SymbolType symbol : SymbolType.values()) {
                    for (GridMatchOrientation orientation : GridMatchOrientation.values()) {
                        setUp();
                        controller.setSelectedMode(mode);
                        controller.setConfiguredMomentum(momentum);
                        start(0);
                        SymbolType other = symbol == ONE ? TWO : ONE;
                        GridPosition first;
                        GridPosition second;
                        GridPosition[] matched;
                        SymbolType[] expected;
                        if (orientation == GridMatchOrientation.HORIZONTAL) {
                            controller.setDebugBoard(symbol, symbol, other, null, null, symbol, null, null, null);
                            first = new GridPosition(0, 2);
                            second = new GridPosition(1, 2);
                            matched = new GridPosition[] {new GridPosition(0, 0), new GridPosition(0, 1), first};
                            expected = new SymbolType[] {symbol, symbol, symbol, null, null, other, null, null, null};
                        } else if (orientation == GridMatchOrientation.VERTICAL) {
                            controller.setDebugBoard(symbol, null, null, symbol, null, null, other, symbol, null);
                            first = new GridPosition(2, 0);
                            second = new GridPosition(2, 1);
                            matched = new GridPosition[] {new GridPosition(0, 0), new GridPosition(1, 0), first};
                            expected = new SymbolType[] {symbol, null, null, symbol, null, null, symbol, other, null};
                        } else {
                            controller.setDebugBoard(symbol, null, null, null, symbol, null, null, symbol, other);
                            first = new GridPosition(2, 1);
                            second = new GridPosition(2, 2);
                            matched = new GridPosition[] {new GridPosition(0, 0), new GridPosition(1, 1), second};
                            expected = new SymbolType[] {symbol, null, null, null, symbol, null, null, other, symbol};
                        }
                        assertTrue(controller.findMatches().isEmpty());
                        provider.add(SIX, ONE, TWO, THREE, FOUR);
                        controller.reserveNextToken();
                        assertTrue(controller.beginSwapSelection(first));

                        MatchResolution resolution = controller.performSwap(first, second);

                        boolean matches = orientation == GridMatchOrientation.DIAGONAL
                                ? mode != TestMode.NORMAL : mode != TestMode.CURSED;
                        int successes = matches && (symbol == FIVE || symbol == SIX) ? 1 : 0;
                        int bonus = matches && (momentum || successes == 0) ? 1 : 0;
                        assertResolution(resolution, matches ? 1 : 0, successes);
                        if (matches) {
                            SymbolType[] replacements = {ONE, TWO, THREE};
                            for (int i = 0; i < matched.length; i++) {
                                GridPosition position = matched[i];
                                assertEquals(replacements[i], resolution.getReplacements().get(position));
                                expected[position.getRow() * GridBoard.SIZE + position.getColumn()] = replacements[i];
                            }
                        }
                        assertBoard(expected);
                        assertResources(2, bonus);
                        assertEquals(successes, controller.getSuccesses());
                        assertEquals(GridTestState.CHECKING_MATCHES, controller.getState());
                        assertEquals(matches ? 13 : 10, provider.consumed);
                        controller.releaseNextToken();
                        assertEquals(SIX, controller.getNextToken());
                        assertEquals(0, controller.finish().getMovesUsed());
                    }
                }
            }
        }
    }

    @Test
    public void swapCascadeAwardsEachWaveAndPreservesReservedNextToken() {
        for (boolean momentum : new boolean[] {false, true}) {
            setUp();
            controller.setConfiguredMomentum(momentum);
            start(0);
            controller.setDebugBoard(ONE, ONE, TWO, null, null, ONE, null, null, null);
            provider.add(SIX, TWO, TWO, TWO, FIVE, FIVE, FIVE, ONE, TWO, THREE, FOUR);
            controller.reserveNextToken();
            assertTrue(controller.beginSwapSelection(new GridPosition(0, 2)));

            assertResolution(controller.performSwap(new GridPosition(0, 2), new GridPosition(1, 2)), 1, 0);
            assertBoard(TWO, TWO, TWO, null, null, TWO, null, null, null);
            assertResources(2, 1);
            assertResolution(controller.resolveMatches(controller.findMatches()), 1, 0);
            assertBoard(FIVE, FIVE, FIVE, null, null, TWO, null, null, null);
            assertResources(2, 2);
            assertResolution(controller.resolveMatches(controller.findMatches()), 1, 1);
            assertBoard(ONE, TWO, THREE, null, null, TWO, null, null, null);
            assertResources(2, momentum ? 3 : 2);
            assertEquals(1, controller.getSuccesses());
            assertTrue(controller.findMatches().isEmpty());
            assertEquals(19, provider.consumed);
            controller.releaseNextToken();
            assertEquals(SIX, controller.getNextToken());
            controller.setState(GridTestState.WAITING_FOR_INPUT);
            assertEquals(SIX, controller.applyMove(new GridMove(GridMoveType.ROW_LEFT, 2)).getIncomingSymbol());
            assertEquals(FOUR, controller.getNextToken());
            assertEquals(1, controller.finish().getMovesUsed());
        }
    }

    @Test
    public void restartResetsConfiguredChargesAndAppliesModeOnlyOnRestart() {
        controller.setInitialSwapCount(2);
        start(4);
        assertEquals(2, controller.getInitialSwapCount());
        assertTrue(controller.beginSwapSelection(new GridPosition(1, 0)));
        controller.performSwap(new GridPosition(1, 0), new GridPosition(1, 1));
        assertResources(1, 4);

        controller.setSelectedMode(TestMode.BLESSED);
        assertEquals(TestMode.NORMAL, controller.getActiveMode());
        assertResources(1, 4);
        start(4);
        assertEquals(TestMode.BLESSED, controller.getActiveMode());
        assertResources(2, 4);
    }

    @Test(expected = IllegalArgumentException.class)
    public void negativeInitialSwapCountIsRejected() {
        controller.setInitialSwapCount(-1);
    }

    private void start(int moves) {
        controller.startTest(moves);
        controller.setState(GridTestState.WAITING_FOR_INPUT);
    }

    private void assertSwapRejected() {
        try {
            controller.performSwap(new GridPosition(1, 0), new GridPosition(1, 1));
            fail("Swap must be rejected");
        } catch (IllegalStateException expected) {
            assertEquals(0, controller.getSuccesses());
        }
    }

    private void assertResources(int swaps, int moves) {
        assertEquals(swaps, controller.getSwapRemaining());
        assertEquals(moves, controller.getMovesRemaining());
        assertEquals(1, controller.getRemainingRerolls());
        assertEquals(1, controller.getSuperRerollsRemaining());
        assertEquals(1, controller.getPickupsAvailable());
    }

    private void assertBoard(SymbolType... expected) {
        for (int i = 0; i < expected.length; i++) {
            assertEquals("Cell " + i, expected[i], controller.getBoard().getCell(i / GridBoard.SIZE, i % GridBoard.SIZE));
        }
    }

    private static void assertResolution(MatchResolution resolution, int lines, int successes) {
        assertEquals(lines, resolution.getMatchedLines());
        assertEquals(successes, resolution.getSuccessesGained());
        assertEquals(lines * 3, resolution.getReplacements().size());
    }

    private static SymbolType[] stableBoard() {
        return new SymbolType[] {ONE, TWO, THREE, FOUR, FIVE, SIX, TWO, THREE, FOUR};
    }

    private static class QueueProvider implements SymbolRandomProvider {
        private final Deque<SymbolType> symbols = new ArrayDeque<>();
        private final SymbolType[] fallback = stableBoard();
        private int index;
        private int consumed;
        private SymbolType reserved;

        void add(SymbolType... values) {
            symbols.addAll(Arrays.asList(values));
        }

        @Override
        public SymbolType peekNext() {
            return symbols.isEmpty() ? fallback[index % fallback.length] : symbols.peekFirst();
        }

        @Override
        public SymbolType next() {
            consumed++;
            return symbols.isEmpty() ? fallback[index++ % fallback.length] : symbols.removeFirst();
        }

        @Override
        public void reserveNextToken() {
            reserved = next();
        }

        @Override
        public void releaseNextToken() {
            symbols.addFirst(reserved);
            reserved = null;
        }

        @Override
        public void clearNextTokenReservation() {
            reserved = null;
        }
    }
}
