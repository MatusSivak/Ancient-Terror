package sk.sivak.eldritchhorror.core.view.components.skilltestgrid;

import org.junit.Test;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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
        controller.setInitialInsertCount(0);
        controller.setInitialPickupCount(0);
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
