package sk.sivak.eldritchhorror.core.view.components.skilltestgrid;

import java.util.Random;

public class FocusReroller {
    private final SymbolReroller symbolReroller;

    public FocusReroller(Random random) {
        symbolReroller = new SymbolReroller(random);
    }

    public SymbolType reroll(SymbolType previousSymbol) {
        return symbolReroller.reroll(previousSymbol);
    }
}
