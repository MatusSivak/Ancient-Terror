package sk.sivak.eldritchhorror.core.view.components.skilltestgrid;

public class GridTestResult {
    public enum Outcome {
        SUCCESS,
        FAILURE,
        SCORE_ONLY
    }

    private final int successes;
    private final int movesUsed;
    private final GridSuccessTarget successTarget;

    public GridTestResult(int successes, int movesUsed) {
        this(successes, movesUsed, GridSuccessTarget.UNLIMITED);
    }

    public GridTestResult(int successes, int movesUsed, GridSuccessTarget target) {
        if (target == null) {
            throw new IllegalArgumentException("target must not be null");
        }
        this.successes = successes;
        this.movesUsed = movesUsed;
        this.successTarget = target;
    }

    public int getSuccesses() {
        return successes;
    }

    public int getMovesUsed() {
        return movesUsed;
    }

    public GridSuccessTarget getSuccessTarget() {
        return successTarget;
    }

    public Outcome getOutcome() {
        if (successTarget.isUnlimited()) {
            return Outcome.SCORE_ONLY;
        }
        return successes >= successTarget.getMinimumSuccesses() ? Outcome.SUCCESS : Outcome.FAILURE;
    }
}
