package sk.sivak.eldritchhorror.core.view.components.skilltestgrid;

final class GridBoardGeometry {
    private final float logicalCellSize;
    private final float cellSize;
    private final float origin;

    GridBoardGeometry(float logicalCellSize, float symbolGap) {
        if (!Float.isFinite(logicalCellSize) || logicalCellSize < 0f
                || !Float.isFinite(symbolGap) || symbolGap < 0f) {
            throw new IllegalArgumentException("Cell size and symbol gap must be finite and nonnegative");
        }
        this.logicalCellSize = logicalCellSize;
        cellSize = logicalCellSize + symbolGap / 2f;
        origin = (logicalCellSize - cellSize) * GridBoard.SIZE / 2f;
    }

    float getCellSize() {
        return cellSize;
    }

    float getOrigin() {
        return origin;
    }

    float getSize() {
        return cellSize * GridBoard.SIZE;
    }

    boolean contains(float x, float y) {
        return logicalCellSize > 0f && x >= origin && x < origin + getSize()
                && y >= origin && y < origin + getSize();
    }

    int columnAt(float x) {
        return Math.max(0, Math.min(GridBoard.SIZE - 1, (int) Math.floor((x - origin) / cellSize)));
    }

    int rowAt(float y) {
        int fromBottom = Math.max(0, Math.min(GridBoard.SIZE - 1, (int) Math.floor((y - origin) / cellSize)));
        return GridBoard.SIZE - 1 - fromBottom;
    }

    float centerX(int column) {
        return origin + (column + 0.5f) * cellSize;
    }

    float centerY(int row) {
        return origin + (GridBoard.SIZE - row - 0.5f) * cellSize;
    }
}
