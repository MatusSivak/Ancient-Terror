package sk.sivak.eldritchhorror.core.view.components.skilltestgrid;

import java.util.Random;

public class RandomSymbolProvider implements SymbolRandomProvider {
    private final Random random;
    private SymbolType nextSymbol;
    private SymbolType reservedNextSymbol;

    public RandomSymbolProvider(Random random) {
        if (random == null) {
            throw new IllegalArgumentException("random must not be null");
        }
        this.random = random;
    }

    public void setSeed(long seed) {
        random.setSeed(seed);
        nextSymbol = null;
        reservedNextSymbol = null;
    }

    @Override
    public SymbolType peekNext() {
        if (reservedNextSymbol != null) {
            return reservedNextSymbol;
        }
        if (nextSymbol == null) {
            nextSymbol = SymbolType.random(random);
        }
        return nextSymbol;
    }

    @Override
    public SymbolType next() {
        if (reservedNextSymbol != null) {
            return SymbolType.random(random);
        }
        SymbolType symbol = peekNext();
        nextSymbol = null;
        return symbol;
    }

    public void reserveNextToken() {
        if (reservedNextSymbol != null) {
            throw new IllegalStateException("Next Token is already reserved");
        }
        reservedNextSymbol = peekNext();
        nextSymbol = null;
    }

    public void releaseNextToken() {
        if (reservedNextSymbol == null) {
            throw new IllegalStateException("Next Token is not reserved");
        }
        nextSymbol = reservedNextSymbol;
        reservedNextSymbol = null;
    }

    public void clearNextTokenReservation() {
        reservedNextSymbol = null;
    }
}
