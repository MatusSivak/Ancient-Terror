package sk.sivak.eldritchhorror.core.view.components.skilltestgrid;

import java.util.Random;

public class RandomSymbolProvider implements SymbolRandomProvider {
    private final Random random;
    private SymbolType nextSymbol;

    public RandomSymbolProvider(Random random) {
        if (random == null) {
            throw new IllegalArgumentException("random must not be null");
        }
        this.random = random;
    }

    public void setSeed(long seed) {
        random.setSeed(seed);
        nextSymbol = null;
    }

    @Override
    public SymbolType peekNext() {
        if (nextSymbol == null) {
            nextSymbol = SymbolType.random(random);
        }
        return nextSymbol;
    }

    @Override
    public SymbolType next() {
        SymbolType symbol = peekNext();
        nextSymbol = null;
        return symbol;
    }
}
