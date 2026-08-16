package sk.sivak.eldritchhorror.core.view.components.skilltestgrid;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class GridTestController {
    private final GridBoard board;
    private GridTestState state = GridTestState.INITIALIZING;
    private int startingMoves;
    private int movesRemaining;
    private int successes;
    private boolean playerMoveResolutionActive;

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
        playerMoveResolutionActive = false;
        state = GridTestState.INITIALIZING;
        board.generateRandomBoard();
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
        playerMoveResolutionActive = true;
        state = GridTestState.SHIFTING;
        return board.shift(move);
    }

    public List<GridMatch> findMatches() {
        return board.findMatches();
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
            } else if (playerMoveResolutionActive) {
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
            playerMoveResolutionActive = false;
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

    public boolean shouldFinishWhenStable() {
        return movesRemaining == 0;
    }

    public GridTestResult finish() {
        state = GridTestState.FINISHED;
        return new GridTestResult(successes, startingMoves - movesRemaining);
    }

    public static class MatchResolution {
        private final Map<GridPosition, SymbolType> replacements;
        private final int successesGained;
        private final int matchedLines;

        public MatchResolution(Map<GridPosition, SymbolType> replacements, int successesGained, int matchedLines) {
            this.replacements = Collections.unmodifiableMap(new LinkedHashMap<>(replacements));
            this.successesGained = successesGained;
            this.matchedLines = matchedLines;
        }

        public Map<GridPosition, SymbolType> getReplacements() {
            return replacements;
        }

        public int getSuccessesGained() {
            return successesGained;
        }

        public int getMatchedLines() {
            return matchedLines;
        }
    }
}
