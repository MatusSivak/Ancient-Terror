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
    private int configuredGapCount;
    private boolean neutralMatchMoveRewardsEnabled;
    private TestMode selectedMode = TestMode.NORMAL;
    private TestMode activeMode = TestMode.NORMAL;
    private boolean configuredMomentum;
    private boolean activeMomentum;
    private boolean configuredBlindEnabled;
    private boolean blindEnabled;
    private GridMove committedBlindMove;
    private static final int DEFAULT_STARTING_REROLLS = 1;
    private int startingRerolls = DEFAULT_STARTING_REROLLS;
    private int remainingRerolls;
    private static final int INITIAL_SWAP_COUNT = 1;
    private int initialSwapCount = INITIAL_SWAP_COUNT;
    private int swapRemaining;
    private static final int INITIAL_SPIN_COUNT = 1;
    private int initialSpinCount = INITIAL_SPIN_COUNT;
    private int remainingSpins;
    private static final int INITIAL_SUPER_REROLL_COUNT = 1;
    private int initialSuperRerollCount = INITIAL_SUPER_REROLL_COUNT;
    private int superRerollsRemaining;
    private static final int INITIAL_INSERT_COUNT = 1;
    private int initialInsertCount = INITIAL_INSERT_COUNT;
    private int insertsAvailable;
    private static final int INITIAL_PICKUP_COUNT = 1;
    private int initialPickupCount = INITIAL_PICKUP_COUNT;
    private int pickupsAvailable;

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
        blindEnabled = configuredBlindEnabled;
        committedBlindMove = null;
        remainingRerolls = startingRerolls;
        swapRemaining = initialSwapCount;
        remainingSpins = initialSpinCount;
        superRerollsRemaining = initialSuperRerollCount;
        insertsAvailable = initialInsertCount;
        pickupsAvailable = initialPickupCount;
        board.generateRandomBoard(activeMode, configuredGapCount);
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

    public void commitBlindMove(GridMove move) {
        if (move == null) {
            throw new IllegalArgumentException("move must not be null");
        }
        if (!blindEnabled) {
            throw new IllegalStateException("Blind is not active");
        }
        if (state != GridTestState.WAITING_FOR_INPUT) {
            throw new IllegalStateException("Cannot commit Blind move in state " + state);
        }
        if (movesRemaining <= 0) {
            throw new IllegalStateException("No moves remaining");
        }
        committedBlindMove = move;
        state = GridTestState.REVEALING_NEXT_TOKEN;
    }

    public GridShiftOutcome applyCommittedBlindMove() {
        if (state != GridTestState.REVEALING_NEXT_TOKEN || committedBlindMove == null) {
            throw new IllegalStateException("No Blind move is awaiting insertion");
        }
        GridMove move = committedBlindMove;
        committedBlindMove = null;
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
            boolean scoringMatch = match.isScoringMatch();
            if (scoringMatch) {
                scoringLines++;
            }
            if (activeMomentum || (!scoringMatch && neutralMatchMoveRewardsEnabled)) {
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

    public void setConfiguredGapCount(int gapCount) {
        if (gapCount < 0 || gapCount > GridBoard.SIZE) {
            throw new IllegalArgumentException("gapCount must be between 0 and " + GridBoard.SIZE);
        }
        configuredGapCount = gapCount;
    }

    public int getConfiguredGapCount() {
        return configuredGapCount;
    }

    public int getGapCount() {
        return board.getGapCount();
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

    public void setConfiguredBlindEnabled(boolean enabled) {
        configuredBlindEnabled = enabled;
    }

    public boolean isConfiguredBlindEnabled() {
        return configuredBlindEnabled;
    }

    public boolean isBlindEnabled() {
        return blindEnabled;
    }

    public GridMove getCommittedBlindMove() {
        return committedBlindMove;
    }

    public int getRemainingRerolls() {
        return remainingRerolls;
    }

    public boolean canActivateReroll() {
        return state == GridTestState.WAITING_FOR_INPUT
                && movesRemaining > 0
                && remainingRerolls > 0
                && findMatches().isEmpty();
    }

    public boolean beginRerollTargeting() {
        if (!canActivateReroll()) {
            return false;
        }
        state = GridTestState.REROLL_SELECTING;
        return true;
    }

    public boolean cancelRerollTargeting() {
        if (state != GridTestState.REROLL_SELECTING) {
            return false;
        }
        state = GridTestState.WAITING_FOR_INPUT;
        return true;
    }

    public SymbolType performReroll(GridPosition position, SymbolReroller reroller) {
        if (state != GridTestState.REROLL_SELECTING) {
            throw new IllegalStateException("Cannot select a Reroll target in state " + state);
        }
        if (remainingRerolls <= 0) {
            throw new IllegalStateException("No Rerolls remaining");
        }
        SymbolType rerolled = board.reroll(position, reroller);
        remainingRerolls--;
        neutralMatchMoveRewardsEnabled = true;
        state = GridTestState.CHECKING_MATCHES;
        return rerolled;
    }

    public int getStartingRerolls() {
        return startingRerolls;
    }

    public void setStartingRerolls(int count) {
        if (count < 0) {
            throw new IllegalArgumentException("startingRerolls must be >= 0");
        }
        startingRerolls = count;
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

    public int getSpinRemaining() {
        return remainingSpins;
    }

    public int getRemainingSpins() {
        return remainingSpins;
    }

    public int getInitialSpinCount() {
        return initialSpinCount;
    }

    public void setInitialSpinCount(int count) {
        if (count < 0) {
            throw new IllegalArgumentException("initialSpinCount must be >= 0");
        }
        initialSpinCount = count;
    }

    public boolean canUseSpin() {
        return state == GridTestState.WAITING_FOR_INPUT
                && movesRemaining > 0
                && remainingSpins > 0;
    }

    public boolean beginSpin() {
        if (!canUseSpin()) {
            return false;
        }
        remainingSpins--;
        neutralMatchMoveRewardsEnabled = true;
        state = GridTestState.SPINNING;
        return true;
    }

    public void completeSpin() {
        if (state != GridTestState.SPINNING) {
            throw new IllegalStateException("Cannot complete Spin in state " + state);
        }
        board.rotateOuterClockwise();
        state = GridTestState.CHECKING_MATCHES;
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

    public int getInsertsAvailable() {
        return insertsAvailable;
    }

    public int getInitialInsertCount() {
        return initialInsertCount;
    }

    public void setInitialInsertCount(int count) {
        if (count < 0) {
            throw new IllegalArgumentException("initialInsertCount must be >= 0");
        }
        initialInsertCount = count;
    }

    public SymbolType getNextToken() {
        return board.getNextToken();
    }

    public void reserveNextToken() {
        board.reserveNextToken();
    }

    public void releaseNextToken() {
        board.releaseNextToken();
    }

    public boolean canUseInsert() {
        return state == GridTestState.WAITING_FOR_INPUT
                && insertsAvailable > 0
                && !blindEnabled
                && board.getNextToken() != null;
    }

    public boolean startInsertMode() {
        if (!canUseInsert()) {
            return false;
        }
        state = GridTestState.INSERT_SELECTING;
        return true;
    }

    public boolean cancelInsertMode() {
        if (state != GridTestState.INSERT_SELECTING) {
            return false;
        }
        state = GridTestState.WAITING_FOR_INPUT;
        return true;
    }

    public SymbolType insertNextToken(int row, int column) {
        if (state != GridTestState.INSERT_SELECTING) {
            throw new IllegalStateException("Cannot select an Insert target in state " + state);
        }
        if (insertsAvailable <= 0) {
            throw new IllegalStateException("No Inserts remaining");
        }
        SymbolType insertedToken = board.insertNextToken(new GridPosition(row, column));
        insertsAvailable--;
        neutralMatchMoveRewardsEnabled = true;
        state = GridTestState.CHECKING_MATCHES;
        return insertedToken;
    }

    public int getPickupsAvailable() {
        return pickupsAvailable;
    }

    public int getInitialPickupCount() {
        return initialPickupCount;
    }

    public void setInitialPickupCount(int count) {
        if (count < 0) {
            throw new IllegalArgumentException("initialPickupCount must be >= 0");
        }
        initialPickupCount = count;
    }

    public boolean canUsePickup() {
        return state == GridTestState.WAITING_FOR_INPUT
                && pickupsAvailable > 0
                && board.getNextToken() != null
                && board.hasOccupiedCell();
    }

    public boolean startPickupMode() {
        if (!canUsePickup()) {
            return false;
        }
        state = GridTestState.PICKUP_SELECTING;
        return true;
    }

    public boolean cancelPickupMode() {
        if (state != GridTestState.PICKUP_SELECTING) {
            return false;
        }
        state = GridTestState.WAITING_FOR_INPUT;
        return true;
    }

    public SymbolType pickupToken(int row, int column) {
        if (state != GridTestState.PICKUP_SELECTING) {
            throw new IllegalStateException("Cannot select a Pickup target in state " + state);
        }
        if (pickupsAvailable <= 0) {
            throw new IllegalStateException("No Pickups remaining");
        }
        SymbolType pickedUpToken = board.pickup(new GridPosition(row, column));
        pickupsAvailable--;
        state = GridTestState.WAITING_FOR_INPUT;
        return pickedUpToken;
    }

    private boolean isValidAdjacentPair(GridPosition pos1, GridPosition pos2) {
        int rowDiff = Math.abs(pos1.getRow() - pos2.getRow());
        int colDiff = Math.abs(pos1.getColumn() - pos2.getColumn());
        return (rowDiff == 1 && colDiff == 0) || (rowDiff == 0 && colDiff == 1);
    }

    public boolean shouldFinishWhenStable() {
        return movesRemaining == 0
                && (insertsAvailable == 0 || blindEnabled || board.getNextToken() == null)
                && (pickupsAvailable == 0 || board.getNextToken() == null || !board.hasOccupiedCell());
    }

    public GridTestResult finish() {
        state = GridTestState.FINISHED;
        return new GridTestResult(successes, startingMoves - movesRemaining);
    }
}
