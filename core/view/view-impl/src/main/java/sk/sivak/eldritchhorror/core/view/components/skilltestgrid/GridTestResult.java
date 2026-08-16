package sk.sivak.eldritchhorror.core.view.components.skilltestgrid;

public class GridTestResult {
    private final int successes;
    private final int movesUsed;

    public GridTestResult(int successes, int movesUsed) {
        this.successes = successes;
        this.movesUsed = movesUsed;
    }

    public int getSuccesses() {
        return successes;
    }

    public int getMovesUsed() {
        return movesUsed;
    }
}
