package sk.sivak.eldritchhorror.core.view.components.skilltestgrid;

import java.util.Random;

public class RandomSymbolProvider implements SymbolRandomProvider {
    private final Random random;

    public RandomSymbolProvider(Random random) {
        if (random == null) {
            throw new IllegalArgumentException("random must not be null");
        }
        this.random = random;
    }

    public void setSeed(long seed) {
        random.setSeed(seed);
    }

    @Override
    public SymbolType next() {
        return SymbolType.random(random);
    }
}
