package sk.sivak.eldritchhorror.core.view.components.skilltestgrid;

import java.util.Objects;

public class GridPosition {
    private final int row;
    private final int column;

    public GridPosition(int row, int column) {
        if (row < 0 || row >= GridBoard.SIZE) {
            throw new IllegalArgumentException("row out of range: " + row);
        }
        if (column < 0 || column >= GridBoard.SIZE) {
            throw new IllegalArgumentException("column out of range: " + column);
        }
        this.row = row;
        this.column = column;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

    @Override
    public String toString() {
        return "GridPosition{" +
                "row=" + row +
                ", column=" + column +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GridPosition)) return false;
        GridPosition that = (GridPosition) o;
        return row == that.row && column == that.column;
    }

    @Override
    public int hashCode() {
        return Objects.hash(row, column);
    }
}
