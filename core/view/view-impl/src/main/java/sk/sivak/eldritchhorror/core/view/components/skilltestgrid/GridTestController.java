package sk.sivak.eldritchhorror.core.view.components.skilltestgrid;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class GridTestController {
    private final GridBoard board;
    private GridTestState state = GridTestState.INITIALIZING;
    private int movesUsed;
    private int movesRemaining;
    private int successes;
    private GridSuccessTarget successTarget = GridSuccessTarget.UNLIMITED;
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
    private static final int INITIAL_SWAP_COUNT = 3;
    private int initialSwapCount = INITIAL_SWAP_COUNT;
    private int swapRemaining;
    private static final int INITIAL_SUPER_REROLL_COUNT = 1;
    private int initialSuperRerollCount = INITIAL_SUPER_REROLL_COUNT;
    private int superRerollsRemaining;
    private static final int INITIAL_PICKUP_COUNT = 1;
    private int initialPickupCount = INITIAL_PICKUP_COUNT;
    private int pickupsAvailable;

    public GridTestController(GridBoard board) {
        if (board == null) {
            throw new IllegalArgumentException("board must not be null");
        }
        this.board = board;
    }

    public void startTest(GridTestParameters parameters) {
        if (parameters == null) {
            throw new IllegalArgumentException("parameters must not be null");
        }
        setInitialSwapCount(parameters.getSwaps());
        setStartingRerolls(parameters.getRerolls());
        setInitialPickupCount(parameters.getLifts());
        setInitialSuperRerollCount(parameters.getSuperRerolls());
        setConfiguredGapCount(parameters.getGaps());
        setConfiguredMomentum(parameters.isMomentum());
        setConfiguredBlindEnabled(parameters.isBlind());
        setSelectedMode(parameters.getDifficulty());
        startTest(parameters.getShifts());
        successTarget = parameters.getSuccessTarget();
    }

    public void startTest(int moves) {
        if (moves < 0) {
            throw new IllegalArgumentException("moves must be >= 0");
        }
        movesUsed = 0;
        movesRemaining = moves;
        successes = 0;
        successTarget = GridSuccessTarget.UNLIMITED;
        neutralMatchMoveRewardsEnabled = false;
        state = GridTestState.INITIALIZING;
        activeMode = selectedMode;
        activeMomentum = configuredMomentum;
        blindEnabled = configuredBlindEnabled;
        committedBlindMove = null;
        remainingRerolls = startingRerolls;
        swapRemaining = initialSwapCount;
        superRerollsRemaining = initialSuperRerollCount;
        pickupsAvailable = initialPickupCount;
        board.generateRandomBoard(activeMode, configuredGapCount);
    }

    public void setDebugBoard(SymbolType... cells) {
        board.setBoard(cells);
    }

    public GridShiftOutcome applyMove(GridMove move) {
        requireUnreachedTarget();
        if (state != GridTestState.WAITING_FOR_INPUT) {
            throw new IllegalStateException("Cannot apply move in state " + state);
        }
        if (movesRemaining <= 0) {
            throw new IllegalStateException("No moves remaining");
        }
        GridShiftOutcome outcome = board.shift(move);
        movesRemaining--;
        movesUsed++;
        neutralMatchMoveRewardsEnabled = true;
        state = GridTestState.SHIFTING;
        return outcome;
    }

    public void commitBlindMove(GridMove move) {
        requireUnreachedTarget();
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
        requireUnreachedTarget();
        if (state != GridTestState.REVEALING_NEXT_TOKEN || committedBlindMove == null) {
            throw new IllegalStateException("No Blind move is awaiting insertion");
        }
        GridMove move = committedBlindMove;
        GridShiftOutcome outcome = board.shift(move);
        committedBlindMove = null;
        movesRemaining--;
        movesUsed++;
        neutralMatchMoveRewardsEnabled = true;
        state = GridTestState.SHIFTING;
        return outcome;
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
        return state == GridTestState.WAITING_FOR_INPUT && movesRemaining > 0 && !hasReachedSuccessTarget();
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

    public GridSuccessTarget getSuccessTarget() {
        return successTarget;
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
                && !hasReachedSuccessTarget()
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
        requireUnreachedTarget();
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

    public boolean beginSwapSelection(GridPosition position) {
        if (position == null) {
            throw new IllegalArgumentException("position must not be null");
        }
        if (state != GridTestState.WAITING_FOR_INPUT || hasReachedSuccessTarget()
                || swapRemaining <= 0 || board.isGap(position)) {
            return false;
        }
        state = GridTestState.SWAP_SELECTING;
        return true;
    }

    public boolean cancelSwapSelection() {
        if (state != GridTestState.SWAP_SELECTING) {
            return false;
        }
        setState(GridTestState.WAITING_FOR_INPUT);
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
        return state == GridTestState.WAITING_FOR_INPUT && !hasReachedSuccessTarget() && hasAvailableSuperReroll();
    }

    private boolean hasAvailableSuperReroll() {
        return superRerollsRemaining > 0 && board.hasSuperRerollCandidates();
    }

    public Map<GridPosition, SymbolType> performSuperReroll(SymbolReroller reroller) {
        requireUnreachedTarget();
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
        requireUnreachedTarget();
        if (state != GridTestState.SWAP_SELECTING) {
            throw new IllegalStateException("Cannot swap in state " + state);
        }
        if (swapRemaining <= 0) {
            throw new IllegalStateException("No Swaps remaining");
        }
        if (!isValidAdjacentPair(pos1, pos2)) {
            throw new IllegalArgumentException("Positions must be orthogonally adjacent");
        }
        if (board.isGap(pos1)) {
            throw new IllegalArgumentException("Swap origin must contain a token");
        }
        board.swap(pos1, pos2);
        swapRemaining--;
        neutralMatchMoveRewardsEnabled = true;
        state = GridTestState.CHECKING_MATCHES;
        List<GridMatch> matches = findMatches();
        return resolveMatches(matches);
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
        return state == GridTestState.WAITING_FOR_INPUT && !hasReachedSuccessTarget() && hasAvailablePickup();
    }

    private boolean hasAvailablePickup() {
        return pickupsAvailable > 0
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
        requireUnreachedTarget();
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
        if (pos1 == null || pos2 == null) {
            return false;
        }
        int rowDiff = Math.abs(pos1.getRow() - pos2.getRow());
        int colDiff = Math.abs(pos1.getColumn() - pos2.getColumn());
        return (rowDiff == 1 && colDiff == 0) || (rowDiff == 0 && colDiff == 1);
    }

    public boolean shouldFinishWhenStable() {
        return hasReachedSuccessTarget() || (movesRemaining == 0
                && !(swapRemaining > 0 && board.hasOccupiedCell())
                && !hasAvailableSuperReroll()
                && !hasAvailablePickup());
    }

    public boolean hasReachedSuccessTarget() {
        return !successTarget.isUnlimited() && successes >= successTarget.getMinimumSuccesses();
    }

    private void requireUnreachedTarget() {
        if (hasReachedSuccessTarget()) {
            throw new IllegalStateException("Test is already successful");
        }
    }

    public GridTestResult finish() {
        state = GridTestState.FINISHED;
        return new GridTestResult(successes, movesUsed, successTarget);
    }
}
