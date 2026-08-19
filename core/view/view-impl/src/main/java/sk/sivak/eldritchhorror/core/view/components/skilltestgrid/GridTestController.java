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
    private boolean neutralMatchMoveRewardsEnabled;
    private TestMode selectedMode = TestMode.NORMAL;
    private TestMode activeMode = TestMode.NORMAL;
    private boolean configuredMomentum;
    private boolean activeMomentum;
    private static final int INITIAL_FOCUS_COUNT = 1;
    private int initialFocusCount = INITIAL_FOCUS_COUNT;
    private int focusRemaining;
    private static final int INITIAL_SWAP_COUNT = 1;
    private int initialSwapCount = INITIAL_SWAP_COUNT;
    private int swapRemaining;
    private static final int INITIAL_SUPER_REROLL_COUNT = 1;
    private int initialSuperRerollCount = INITIAL_SUPER_REROLL_COUNT;
    private int superRerollsRemaining;

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
        neutralMatchMoveRewardsEnabled = false;
        state = GridTestState.INITIALIZING;
        activeMode = selectedMode;
        activeMomentum = configuredMomentum;
        focusRemaining = initialFocusCount;
        swapRemaining = initialSwapCount;
        superRerollsRemaining = initialSuperRerollCount;
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
        neutralMatchMoveRewardsEnabled = true;
        state = GridTestState.SHIFTING;
        return board.shift(move);
    }

    public List<GridMatch> findMatches() {
        return board.findMatches(activeMode);
    }

    public MatchResolution resolveMatches(List<GridMatch> matches) {
        if (matches == null || matches.isEmpty()) {
            return new MatchResolution(Collections.emptyMap(), 0, 0, Collections.emptyList());
        }
        int scoringLines = 0;
        int bonusMovesGained = 0;
        for (GridMatch match : matches) {
            if (match.isScoringMatch()) {
                scoringLines++;
                if (activeMomentum) {
                    bonusMovesGained++;
                }
            } else if (neutralMatchMoveRewardsEnabled) {
                bonusMovesGained++;
            }
        }
        movesRemaining += bonusMovesGained;
        successes += scoringLines;
        Map<GridPosition, SymbolType> replacements = board.replaceCells(board.collectMatchedCells(matches));
        return new MatchResolution(replacements, scoringLines, matches.size(), matches);
    }

    public boolean canAcceptInput() {
        return state == GridTestState.WAITING_FOR_INPUT && movesRemaining > 0;
    }

    public void setState(GridTestState state) {
        this.state = state;
        if (state == GridTestState.WAITING_FOR_INPUT || state == GridTestState.FINISHED) {
            neutralMatchMoveRewardsEnabled = false;
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

    public void setConfiguredMomentum(boolean configuredMomentum) {
        this.configuredMomentum = configuredMomentum;
    }

    public boolean isConfiguredMomentum() {
        return configuredMomentum;
    }

    public boolean isActiveMomentum() {
        return activeMomentum;
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

    public int getSwapRemaining() {
        return swapRemaining;
    }

    public boolean useSwap() {
        if (swapRemaining <= 0) {
            return false;
        }
        swapRemaining--;
        return true;
    }

    public int getInitialSwapCount() {
        return initialSwapCount;
    }

    public void setInitialSwapCount(int count) {
        if (count < 0) {
            throw new IllegalArgumentException("initialSwapCount must be >= 0");
        }
        this.initialSwapCount = count;
    }

    public int getSuperRerollsRemaining() {
        return superRerollsRemaining;
    }

    public int getInitialSuperRerollCount() {
        return initialSuperRerollCount;
    }

    public void setInitialSuperRerollCount(int count) {
        if (count < 0) {
            throw new IllegalArgumentException("initialSuperRerollCount must be >= 0");
        }
        initialSuperRerollCount = count;
    }

    public boolean canUseSuperReroll() {
        return state == GridTestState.WAITING_FOR_INPUT
                && superRerollsRemaining > 0
                && board.hasSuperRerollCandidates();
    }

    public Map<GridPosition, SymbolType> performSuperReroll(SymbolReroller reroller) {
        if (reroller == null) {
            throw new IllegalArgumentException("reroller must not be null");
        }
        if (state != GridTestState.WAITING_FOR_INPUT) {
            throw new IllegalStateException("Cannot use Super Reroll in state " + state);
        }
        if (superRerollsRemaining <= 0) {
            return Collections.emptyMap();
        }

        Map<GridPosition, SymbolType> rerolledCells = board.superReroll(reroller);
        if (rerolledCells.isEmpty()) {
            return Collections.emptyMap();
        }

        superRerollsRemaining--;
        neutralMatchMoveRewardsEnabled = true;
        return rerolledCells;
    }

    public MatchResolution performSwap(GridPosition pos1, GridPosition pos2) {
        if (!isValidAdjacentPair(pos1, pos2)) {
            throw new IllegalArgumentException("Positions must be orthogonally adjacent");
        }
        board.swap(pos1, pos2);
        List<GridMatch> matches = findMatches();
        return resolveMatches(matches);
    }

    private boolean isValidAdjacentPair(GridPosition pos1, GridPosition pos2) {
        int rowDiff = Math.abs(pos1.getRow() - pos2.getRow());
        int colDiff = Math.abs(pos1.getColumn() - pos2.getColumn());
        return (rowDiff == 1 && colDiff == 0) || (rowDiff == 0 && colDiff == 1);
    }

    public boolean shouldFinishWhenStable() {
        return movesRemaining == 0;
    }

    public GridTestResult finish() {
        state = GridTestState.FINISHED;
        return new GridTestResult(successes, startingMoves - movesRemaining);
    }
}
