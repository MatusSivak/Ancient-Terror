package sk.sivak.eldritchhorror.core.view.components.skilltestgrid;

import java.util.Random;

public class FocusReroller {
    private static final SymbolType[] ALL_SYMBOLS = SymbolType.values();
    private static final SymbolType[] OTHERS_EXCLUDING_ONE = {
            SymbolType.TWO, SymbolType.THREE, SymbolType.FOUR, SymbolType.FIVE, SymbolType.SIX
    };
    private static final SymbolType[] OTHERS_EXCLUDING_TWO = {
            SymbolType.ONE, SymbolType.THREE, SymbolType.FOUR, SymbolType.FIVE, SymbolType.SIX
    };
    private static final SymbolType[] OTHERS_EXCLUDING_THREE = {
            SymbolType.ONE, SymbolType.TWO, SymbolType.FOUR, SymbolType.FIVE, SymbolType.SIX
    };
    private static final SymbolType[] OTHERS_EXCLUDING_FOUR = {
            SymbolType.ONE, SymbolType.TWO, SymbolType.THREE, SymbolType.FIVE, SymbolType.SIX
    };
    private static final SymbolType[] OTHERS_EXCLUDING_FIVE = {
            SymbolType.ONE, SymbolType.TWO, SymbolType.THREE, SymbolType.FOUR, SymbolType.SIX
    };
    private static final SymbolType[] OTHERS_EXCLUDING_SIX = {
            SymbolType.ONE, SymbolType.TWO, SymbolType.THREE, SymbolType.FOUR, SymbolType.FIVE
    };

    private final Random random;

    public FocusReroller(Random random) {
        if (random == null) {
            throw new IllegalArgumentException("random must not be null");
        }
        this.random = random;
    }

    public SymbolType reroll(SymbolType previousSymbol) {
        if (previousSymbol == null) {
            throw new IllegalArgumentException("previousSymbol must not be null");
        }
        SymbolType[] others = getOthersExcluding(previousSymbol);
        return others[random.nextInt(others.length)];
    }

    private SymbolType[] getOthersExcluding(SymbolType symbol) {
        switch (symbol) {
            case ONE:
                return OTHERS_EXCLUDING_ONE;
            case TWO:
                return OTHERS_EXCLUDING_TWO;
            case THREE:
                return OTHERS_EXCLUDING_THREE;
            case FOUR:
                return OTHERS_EXCLUDING_FOUR;
            case FIVE:
                return OTHERS_EXCLUDING_FIVE;
            case SIX:
                return OTHERS_EXCLUDING_SIX;
            default:
                throw new IllegalArgumentException("Unknown symbol: " + symbol);
        }
    }
}
