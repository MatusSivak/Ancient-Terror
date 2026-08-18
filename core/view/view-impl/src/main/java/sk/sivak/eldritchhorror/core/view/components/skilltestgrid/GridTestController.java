package sk.sivak.eldritchhorror.core.view.components.skilltestgrid;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class GridTestController {
    private final GridBoard board;
    private GridTestState state = GridTestState.INITIALIZING;
    private int startingMoves;
    private int movesRemaining;
    private int successes;
    private boolean isPlayerMoveInResolution;
    private TestMode selectedMode = TestMode.NORMAL;
    private TestMode activeMode = TestMode.NORMAL;
    private static final int INITIAL_FOCUS_COUNT = 3;
    private int initialFocusCount = INITIAL_FOCUS_COUNT;
    private int focusRemaining;

    public GridTestController(GridBoard board) {
        if (board == null) {
            throw new IllegalArgumentException("board must not be null");
        }
        this.board = board;
    }

    public void startTest(int moves) {
        if (moves < 0) {
            throw new IllegalArgumentException("moves must be >= 0");
        }
        startingMoves = moves;
        movesRemaining = moves;
        successes = 0;
        isPlayerMoveInResolution = false;
        state = GridTestState.INITIALIZING;
        activeMode = selectedMode;
        focusRemaining = initialFocusCount;
        board.generateRandomBoard(activeMode);
    }

    public void setDebugBoard(SymbolType... cells) {
        board.setBoard(cells);
    }

    public GridShiftOutcome applyMove(GridMove move) {
        if (state != GridTestState.WAITING_FOR_INPUT) {
            throw new IllegalStateException("Cannot apply move in state " + state);
        }
        if (movesRemaining <= 0) {
            throw new IllegalStateException("No moves remaining");
        }
        movesRemaining--;
        isPlayerMoveInResolution = true;
        state = GridTestState.SHIFTING;
        return board.shift(move);
    }

    public List<GridMatch> findMatches() {
        return board.findMatches(activeMode);
    }

    public MatchResolution resolveMatches(List<GridMatch> matches) {
        if (matches == null || matches.isEmpty()) {
            return new MatchResolution(Collections.emptyMap(), 0, 0);
        }
        int scoringLines = 0;
        int bonusMovesGained = 0;
        for (GridMatch match : matches) {
            if (match.isScoringMatch()) {
                scoringLines++;
            } else if (isPlayerMoveInResolution) {
                bonusMovesGained++;
            }
        }
        movesRemaining += bonusMovesGained;
        successes += scoringLines;
        Map<GridPosition, SymbolType> replacements = board.replaceCells(board.collectMatchedCells(matches));
        return new MatchResolution(replacements, scoringLines, matches.size());
    }

    public boolean canAcceptInput() {
        return state == GridTestState.WAITING_FOR_INPUT && movesRemaining > 0;
    }

    public void setState(GridTestState state) {
        this.state = state;
        if (state == GridTestState.WAITING_FOR_INPUT || state == GridTestState.FINISHED) {
            isPlayerMoveInResolution = false;
        }
    }

    public GridTestState getState() {
        return state;
    }

    public int getMovesRemaining() {
        return movesRemaining;
    }

    public int getSuccesses() {
        return successes;
    }

    public GridBoard getBoard() {
        return board;
    }

    public void setSelectedMode(TestMode selectedMode) {
        if (selectedMode == null) {
            throw new IllegalArgumentException("selectedMode must not be null");
        }
        this.selectedMode = selectedMode;
    }

    public TestMode getSelectedMode() {
        return selectedMode;
    }

    public TestMode getActiveMode() {
        return activeMode;
    }

    public int getFocusRemaining() {
        return focusRemaining;
    }

    public boolean useFocus() {
        if (focusRemaining <= 0) {
            return false;
        }
        focusRemaining--;
        return true;
    }

    public int getInitialFocusCount() {
        return initialFocusCount;
    }

    public void setInitialFocusCount(int count) {
        if (count < 0) {
            throw new IllegalArgumentException("initialFocusCount must be >= 0");
        }
        this.initialFocusCount = count;
    }

    public boolean shouldFinishWhenStable() {
        return movesRemaining == 0;
    }

    public GridTestResult finish() {
        state = GridTestState.FINISHED;
        return new GridTestResult(successes, startingMoves - movesRemaining);
    }
}
