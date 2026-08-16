package sk.sivak.eldritchhorror.core.view.components.skilltestgrid;

import java.util.Objects;

public class GridMove {
    private final GridMoveType type;
    private final int index;

    public GridMove(GridMoveType type, int index) {
        this.type = Objects.requireNonNull(type, "type must not be null");
        if (index < 0 || index >= GridBoard.SIZE) {
            throw new IllegalArgumentException("index out of range: " + index);
        }
        this.index = index;
    }

    public GridMoveType getType() {
        return type;
    }

    public int getIndex() {
        return index;
    }
}
