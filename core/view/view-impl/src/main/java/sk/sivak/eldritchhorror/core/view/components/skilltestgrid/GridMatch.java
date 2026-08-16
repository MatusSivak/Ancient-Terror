package sk.sivak.eldritchhorror.core.view.components.skilltestgrid;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GridMatch {
    private final SymbolType symbol;
    private final List<GridPosition> cells;

    public GridMatch(SymbolType symbol, List<GridPosition> cells) {
        this.symbol = symbol;
        this.cells = Collections.unmodifiableList(new ArrayList<>(cells));
    }

    public SymbolType getSymbol() {
        return symbol;
    }

    public List<GridPosition> getCells() {
        return cells;
    }

    public boolean isScoringMatch() {
        return symbol.isScoring();
    }
}
