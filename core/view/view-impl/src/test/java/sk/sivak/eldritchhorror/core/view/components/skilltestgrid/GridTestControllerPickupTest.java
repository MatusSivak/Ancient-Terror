package sk.sivak.eldritchhorror.core.view.components.skilltestgrid;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.LinkedHashSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class GridTestControllerPickupTest {
    private QueueSymbolProvider provider;
    private GridTestController controller;

    @Before
    public void setUp() {
        provider = new QueueSymbolProvider();
        controller = new GridTestController(new GridBoard(provider));
        controller.setInitialPickupCount(2);
        controller.startTest(5);
        controller.setDebugBoard(
                SymbolType.ONE, SymbolType.TWO, SymbolType.THREE,
                SymbolType.TWO, SymbolType.THREE, SymbolType.FOUR,
                SymbolType.THREE, SymbolType.FOUR, SymbolType.SIX
        );
        controller.setState(GridTestState.WAITING_FOR_INPUT);
    }

    @Test
    public void pickupRemovesOccupiedTokenAndMakesItNextWithoutUsingMove() {
        provider.add(SymbolType.TWO, SymbolType.FOUR);
        int movesBefore = controller.getMovesRemaining();

        assertTrue(controller.startPickupMode());
        assertEquals(SymbolType.SIX, controller.pickupToken(2, 2));

        assertEquals(SymbolType.SIX, controller.getNextToken());
        assertTrue(controller.getBoard().isGap(new GridPosition(2, 2)));
        assertEquals(1, controller.getPickupsAvailable());
        assertEquals(movesBefore, controller.getMovesRemaining());
        assertTrue(controller.findMatches().isEmpty());
    }

    @Test
    public void pickupPreservesPreviousNextAndRemainingSequenceOrder() {
        provider.add(SymbolType.THREE, SymbolType.FIVE, SymbolType.ONE, SymbolType.FOUR);

        controller.startPickupMode();
        controller.pickupToken(2, 2);

        assertEquals(SymbolType.SIX, controller.getNextToken());
        assertEquals(SymbolType.SIX, shiftAndReady().getIncomingSymbol());
        assertEquals(SymbolType.THREE, controller.getNextToken());
        assertEquals(SymbolType.THREE, shiftAndReady().getIncomingSymbol());
        assertEquals(SymbolType.FIVE, controller.getNextToken());
        assertEquals(SymbolType.FIVE, shiftAndReady().getIncomingSymbol());
        assertEquals(SymbolType.ONE, controller.getNextToken());
        assertEquals(SymbolType.ONE, shiftAndReady().getIncomingSymbol());
        assertEquals(SymbolType.FOUR, controller.getNextToken());
    }

    @Test
    public void laterTacticalResolutionDoesNotConsumePickedUpSequence() {
        provider.add(SymbolType.THREE, SymbolType.FIVE);
        controller.startPickupMode();
        controller.pickupToken(2, 2);
        controller.reserveNextToken();

        LinkedHashSet<GridPosition> replacement = new LinkedHashSet<>();
        replacement.add(new GridPosition(0, 0));
        controller.getBoard().replaceCells(replacement);
        controller.releaseNextToken();

        assertEquals(SymbolType.SIX, controller.getNextToken());
        assertEquals(SymbolType.SIX, shiftAndReady().getIncomingSymbol());
        assertEquals(SymbolType.THREE, controller.getNextToken());
    }

    @Test
    public void pickupWithZeroAvailableIsRejected() {
        controller.setInitialPickupCount(0);
        controller.startTest(5);
        controller.setState(GridTestState.WAITING_FOR_INPUT);

        assertFalse(controller.canUsePickup());
        assertFalse(controller.startPickupMode());
    }

    @Test
    public void pickupOfGapIsRejectedWithoutConsumingResource() {
        controller.setDebugBoard(
                SymbolType.ONE, null, SymbolType.THREE,
                SymbolType.TWO, SymbolType.THREE, SymbolType.FOUR,
                SymbolType.THREE, SymbolType.FOUR, SymbolType.SIX
        );
        provider.add(SymbolType.TWO);

        assertTrue(controller.startPickupMode());
        try {
            controller.pickupToken(0, 1);
            fail("Expected an empty position to be rejected");
        } catch (IllegalArgumentException expected) {
            assertEquals(2, controller.getPickupsAvailable());
            assertTrue(controller.getBoard().isGap(new GridPosition(0, 1)));
        }
    }

    @Test
    public void pickupCanBeUsedWithNoMovesRemaining() {
        controller.startTest(0);
        controller.setDebugBoard(
                SymbolType.ONE, SymbolType.TWO, SymbolType.THREE,
                SymbolType.TWO, SymbolType.THREE, SymbolType.FOUR,
                SymbolType.THREE, SymbolType.FOUR, SymbolType.SIX
        );
        controller.setState(GridTestState.WAITING_FOR_INPUT);
        provider.add(SymbolType.TWO);

        assertTrue(controller.canUsePickup());
        controller.startPickupMode();
        controller.pickupToken(2, 2);

        assertEquals(0, controller.getMovesRemaining());
        assertEquals(SymbolType.SIX, controller.getNextToken());
    }

    @Test
    public void pickupGapMovesWithShiftAndIsRemovedWhenShiftedOut() {
        provider.add(SymbolType.TWO, SymbolType.FIVE);
        controller.startPickupMode();
        controller.pickupToken(0, 1);

        GridShiftOutcome firstShift = shiftAndReady();
        assertEquals(SymbolType.TWO, firstShift.getIncomingSymbol());
        assertTrue(controller.getBoard().isGap(new GridPosition(0, 0)));
        assertEquals(1, controller.getBoard().getGapCount());

        GridShiftOutcome secondShift = shiftAndReady();
        assertEquals(SymbolType.TWO, secondShift.getIncomingSymbol());
        assertEquals(0, controller.getBoard().getGapCount());
        assertFalse(controller.getBoard().isGap(new GridPosition(0, 0)));
        assertFalse(controller.getBoard().isGap(new GridPosition(0, 1)));
        assertFalse(controller.getBoard().isGap(new GridPosition(0, 2)));
    }

    @Test
    public void pickupGapNeverCountsAsMatchingToken() {
        controller.setDebugBoard(
                SymbolType.FIVE, SymbolType.FIVE, SymbolType.FIVE,
                SymbolType.ONE, SymbolType.TWO, SymbolType.THREE,
                SymbolType.TWO, SymbolType.THREE, SymbolType.FOUR
        );
        provider.add(SymbolType.TWO);
        assertEquals(1, controller.findMatches().size());

        controller.startPickupMode();
        controller.pickupToken(0, 1);

        assertTrue(controller.findMatches().isEmpty());
        assertEquals(0, controller.getSuccesses());
    }

    @Test
    public void configuredPickupCountResetsWhenTestStarts() {
        controller.setInitialPickupCount(3);
        controller.startTest(5);
        assertEquals(3, controller.getPickupsAvailable());

        controller.setState(GridTestState.WAITING_FOR_INPUT);
        controller.startPickupMode();
        controller.pickupToken(0, 0);
        controller.startTest(5);

        assertEquals(3, controller.getPickupsAvailable());
    }

    @Test(expected = IllegalArgumentException.class)
    public void negativeInitialPickupCountIsRejected() {
        controller.setInitialPickupCount(-1);
    }

    private GridShiftOutcome shiftAndReady() {
        GridShiftOutcome outcome = controller.applyMove(new GridMove(GridMoveType.ROW_LEFT, 0));
        controller.setState(GridTestState.WAITING_FOR_INPUT);
        return outcome;
    }

    private static class QueueSymbolProvider implements SymbolRandomProvider {
        private final Deque<SymbolType> queue = new ArrayDeque<>();
        private final SymbolType[] fallback = {
                SymbolType.ONE, SymbolType.TWO, SymbolType.THREE,
                SymbolType.FOUR, SymbolType.FIVE, SymbolType.SIX
        };
        private int fallbackIndex;
        private SymbolType reserved;

        void add(SymbolType... symbols) {
            queue.addAll(Arrays.asList(symbols));
        }

        @Override
        public SymbolType peekNext() {
            return queue.isEmpty()
                    ? fallback[fallbackIndex % fallback.length]
                    : queue.peekFirst();
        }

        @Override
        public SymbolType next() {
            return queue.isEmpty()
                    ? fallback[fallbackIndex++ % fallback.length]
                    : queue.removeFirst();
        }

        @Override
        public void reserveNextToken() {
            reserved = next();
        }

        @Override
        public void releaseNextToken() {
            queue.addFirst(reserved);
            reserved = null;
        }

        @Override
        public void clearNextTokenReservation() {
            reserved = null;
        }
    }
}
