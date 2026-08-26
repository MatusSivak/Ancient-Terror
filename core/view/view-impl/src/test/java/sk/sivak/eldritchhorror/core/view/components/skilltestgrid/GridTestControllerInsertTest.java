package sk.sivak.eldritchhorror.core.view.components.skilltestgrid;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class GridTestControllerInsertTest {
    private QueueSymbolProvider provider;
    private GridTestController controller;

    @Before
    public void setUp() {
        provider = new QueueSymbolProvider();
        controller = new GridTestController(new GridBoard(provider));
        controller.setInitialInsertCount(2);
        controller.setInitialPickupCount(0);
        controller.startTest(5);
        provider.resetConsumed();
        setStableBoard();
        makeReady();
    }

    @Test
    public void insertConsumesResourceButNotMoveAndReplacesSelectedToken() {
        provider.add(SymbolType.SIX, SymbolType.FOUR);
        SymbolType removedToken = controller.getBoard().getCell(0, 0);
        int movesBefore = controller.getMovesRemaining();

        assertTrue(controller.startInsertMode());
        assertEquals(SymbolType.SIX, controller.insertNextToken(0, 0));

        assertEquals(1, controller.getInsertsAvailable());
        assertEquals(movesBefore, controller.getMovesRemaining());
        assertEquals(SymbolType.SIX, controller.getBoard().getCell(0, 0));
        assertFalse(removedToken == controller.getBoard().getCell(0, 0));
    }

    @Test
    public void insertConsumesCurrentNextTokenAndGeneratesFreshNextToken() {
        provider.add(SymbolType.FIVE, SymbolType.THREE);
        assertEquals(SymbolType.FIVE, controller.getNextToken());

        controller.startInsertMode();
        controller.insertNextToken(2, 2);

        assertEquals(SymbolType.FIVE, controller.getBoard().getCell(2, 2));
        assertEquals(SymbolType.THREE, controller.getNextToken());
        assertEquals(1, provider.consumed);
    }

    @Test
    public void centerCellCanBeTargeted() {
        provider.add(SymbolType.SIX, SymbolType.TWO);

        controller.startInsertMode();
        controller.insertNextToken(1, 1);

        assertEquals(SymbolType.SIX, controller.getBoard().getCell(1, 1));
    }

    @Test
    public void insertingSameSymbolIsAllowed() {
        provider.add(SymbolType.THREE, SymbolType.SIX);

        controller.startInsertMode();
        controller.insertNextToken(1, 1);

        assertEquals(SymbolType.THREE, controller.getBoard().getCell(1, 1));
        assertEquals(1, controller.getInsertsAvailable());
    }

    @Test
    public void insertCreatedNeutralMatchResolvesAndAwardsBonusMove() {
        controller.setDebugBoard(
                SymbolType.ONE, SymbolType.ONE, SymbolType.THREE,
                SymbolType.TWO, SymbolType.THREE, SymbolType.FOUR,
                SymbolType.THREE, SymbolType.FOUR, SymbolType.FIVE
        );
        provider.add(
                SymbolType.ONE, SymbolType.SIX,
                SymbolType.TWO, SymbolType.THREE, SymbolType.FOUR
        );

        controller.startInsertMode();
        controller.insertNextToken(0, 2);
        List<GridMatch> matches = controller.findMatches();
        assertEquals(1, matches.size());

        MatchResolution resolution = controller.resolveMatches(matches);

        assertEquals(1, resolution.getMatchedLines());
        assertEquals(6, controller.getMovesRemaining());
        assertEquals(0, controller.getSuccesses());
    }

    @Test
    public void insertCreatedSuccessMatchUsesNormalRewards() {
        controller.setDebugBoard(
                SymbolType.FIVE, SymbolType.FIVE, SymbolType.THREE,
                SymbolType.ONE, SymbolType.TWO, SymbolType.FOUR,
                SymbolType.TWO, SymbolType.THREE, SymbolType.SIX
        );
        provider.add(
                SymbolType.FIVE, SymbolType.ONE,
                SymbolType.TWO, SymbolType.THREE, SymbolType.FOUR
        );

        controller.startInsertMode();
        controller.insertNextToken(0, 2);
        MatchResolution resolution = controller.resolveMatches(controller.findMatches());

        assertEquals(1, resolution.getSuccessesGained());
        assertEquals(1, controller.getSuccesses());
        assertEquals(5, controller.getMovesRemaining());
    }

    @Test
    public void insertCreatedCascadesUseExistingResolutionPath() {
        controller.setDebugBoard(
                SymbolType.ONE, SymbolType.ONE, SymbolType.THREE,
                SymbolType.THREE, SymbolType.FOUR, SymbolType.FIVE,
                SymbolType.FOUR, SymbolType.FIVE, SymbolType.SIX
        );
        provider.add(
                SymbolType.ONE,
                SymbolType.TWO, SymbolType.TWO, SymbolType.TWO,
                SymbolType.THREE, SymbolType.FOUR, SymbolType.FIVE
        );

        controller.startInsertMode();
        controller.insertNextToken(0, 2);

        int resolvedWaves = resolveAllMatches();

        assertEquals(2, resolvedWaves);
        assertEquals(7, controller.getMovesRemaining());
    }

    @Test
    public void multipleInsertsCanBeUsedConsecutively() {
        provider.add(SymbolType.FIVE, SymbolType.SIX, SymbolType.FOUR);

        controller.startInsertMode();
        controller.insertNextToken(0, 0);
        controller.setState(GridTestState.WAITING_FOR_INPUT);
        assertTrue(controller.startInsertMode());
        controller.insertNextToken(1, 1);

        assertEquals(SymbolType.FIVE, controller.getBoard().getCell(0, 0));
        assertEquals(SymbolType.SIX, controller.getBoard().getCell(1, 1));
        assertEquals(SymbolType.FOUR, controller.getNextToken());
        assertEquals(0, controller.getInsertsAvailable());
        assertEquals(5, controller.getMovesRemaining());
    }

    @Test
    public void cancelingInsertConsumesNothing() {
        provider.add(SymbolType.FIVE);
        int movesBefore = controller.getMovesRemaining();
        int insertsBefore = controller.getInsertsAvailable();

        assertTrue(controller.startInsertMode());
        assertTrue(controller.cancelInsertMode());

        assertEquals(GridTestState.WAITING_FOR_INPUT, controller.getState());
        assertEquals(insertsBefore, controller.getInsertsAvailable());
        assertEquals(movesBefore, controller.getMovesRemaining());
        assertEquals(SymbolType.FIVE, controller.getNextToken());
        assertEquals(0, provider.consumed);
    }

    @Test
    public void insertWorksAtZeroMovesAndDelaysFinalizationUntilSpent() {
        controller.startTest(0);
        setStableBoard();
        makeReady();
        provider.add(SymbolType.SIX, SymbolType.FOUR);

        assertFalse(controller.shouldFinishWhenStable());
        assertTrue(controller.canUseInsert());
        controller.startInsertMode();
        controller.insertNextToken(1, 1);

        assertEquals(0, controller.getMovesRemaining());
        assertEquals(1, controller.getInsertsAvailable());
        assertFalse(controller.shouldFinishWhenStable());

        controller.setState(GridTestState.WAITING_FOR_INPUT);
        controller.startInsertMode();
        controller.insertNextToken(2, 2);
        assertTrue(controller.shouldFinishWhenStable());
    }

    @Test
    public void hiddenNextTokenDoesNotBlockBlindTestFinalization() {
        controller.setConfiguredBlindEnabled(true);
        controller.startTest(0);
        makeReady();

        assertFalse(controller.canUseInsert());
        assertTrue(controller.shouldFinishWhenStable());
    }

    @Test
    public void configuredInsertCountResetsWhenTestStarts() {
        controller.setInitialInsertCount(3);
        controller.startTest(5);
        assertEquals(3, controller.getInsertsAvailable());

        controller.setState(GridTestState.WAITING_FOR_INPUT);
        provider.add(SymbolType.FIVE, SymbolType.SIX);
        controller.startInsertMode();
        controller.insertNextToken(0, 0);
        controller.startTest(5);

        assertEquals(3, controller.getInsertsAvailable());
    }

    @Test(expected = IllegalArgumentException.class)
    public void negativeInitialInsertCountIsRejected() {
        controller.setInitialInsertCount(-1);
    }

    private void setStableBoard() {
        controller.setDebugBoard(
                SymbolType.ONE, SymbolType.TWO, SymbolType.THREE,
                SymbolType.TWO, SymbolType.THREE, SymbolType.FOUR,
                SymbolType.THREE, SymbolType.FOUR, SymbolType.FIVE
        );
    }

    private void makeReady() {
        controller.setState(GridTestState.WAITING_FOR_INPUT);
    }

    private int resolveAllMatches() {
        int waves = 0;
        while (true) {
            List<GridMatch> matches = controller.findMatches();
            if (matches.isEmpty()) {
                return waves;
            }
            controller.resolveMatches(matches);
            waves++;
            if (waves > 20) {
                throw new IllegalStateException("Unexpected infinite cascade");
            }
        }
    }

    private static class QueueSymbolProvider implements SymbolRandomProvider {
        private final Deque<SymbolType> queue = new ArrayDeque<>();
        private final SymbolType[] fallback = {
                SymbolType.ONE, SymbolType.TWO, SymbolType.THREE,
                SymbolType.FOUR, SymbolType.FIVE, SymbolType.SIX
        };
        private int fallbackIndex;
        private int consumed;

        void add(SymbolType... symbols) {
            queue.addAll(Arrays.asList(symbols));
        }

        void resetConsumed() {
            consumed = 0;
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
            consumed++;
            if (!queue.isEmpty()) {
                return queue.removeFirst();
            }
            return fallback[fallbackIndex++ % fallback.length];
        }
    }
}
