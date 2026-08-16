package sk.sivak.eldritchhorror.core.view.components.skilltestgrid;

import java.util.Random;

public enum SymbolType {
    ONE(false),
    TWO(false),
    THREE(false),
    FOUR(false),
    FIVE(true),
    SIX(true);

    private static final SymbolType[] VALUES = values();
    private final boolean scoring;

    SymbolType(boolean scoring) {
        this.scoring = scoring;
    }

    public boolean isScoring() {
        return scoring;
    }

    public static SymbolType random(Random random) {
        return VALUES[random.nextInt(VALUES.length)];
    }
}
