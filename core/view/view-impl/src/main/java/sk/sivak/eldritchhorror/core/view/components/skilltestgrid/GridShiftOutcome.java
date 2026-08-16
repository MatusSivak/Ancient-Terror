package sk.sivak.eldritchhorror.core.view.components.skilltestgrid;

public class GridShiftOutcome {
    private final GridMove move;
    private final SymbolType incomingSymbol;

    public GridShiftOutcome(GridMove move, SymbolType incomingSymbol) {
        this.move = move;
        this.incomingSymbol = incomingSymbol;
    }

    public GridMove getMove() {
        return move;
    }

    public SymbolType getIncomingSymbol() {
        return incomingSymbol;
    }
}
