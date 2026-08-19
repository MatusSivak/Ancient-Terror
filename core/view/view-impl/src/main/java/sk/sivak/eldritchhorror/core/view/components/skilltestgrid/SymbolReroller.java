package sk.sivak.eldritchhorror.core.view.components.skilltestgrid;

import java.util.Random;

public class SymbolReroller {
    private static final SymbolType[] VALUES = SymbolType.values();

    private final Random random;

    public SymbolReroller(Random random) {
        if (random == null) {
            throw new IllegalArgumentException("random must not be null");
        }
        this.random = random;
    }

    public SymbolType reroll(SymbolType previousSymbol) {
        if (previousSymbol == null) {
            throw new IllegalArgumentException("previousSymbol must not be null");
        }
        int index = random.nextInt(VALUES.length - 1);
        if (index >= previousSymbol.ordinal()) {
            index++;
        }
        return VALUES[index];
    }
}
