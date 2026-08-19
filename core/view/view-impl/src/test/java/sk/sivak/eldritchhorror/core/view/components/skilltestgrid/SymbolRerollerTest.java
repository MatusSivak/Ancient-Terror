package sk.sivak.eldritchhorror.core.view.components.skilltestgrid;

import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

public class SymbolRerollerTest {

    @Test
    public void rerollAlwaysGeneratesADifferentSymbol() {
        SymbolReroller reroller = new SymbolReroller(new Random(42L));
        for (SymbolType symbol : SymbolType.values()) {
            for (int i = 0; i < 100; i++) {
                assertNotEquals(symbol, reroller.reroll(symbol));
            }
        }
    }

    @Test
    public void rerollDistributionUsesAllFiveAlternatives() {
        SymbolReroller reroller = new SymbolReroller(new Random(42L));
        int[] counts = new int[SymbolType.values().length];
        int iterations = 5000;

        for (int i = 0; i < iterations; i++) {
            counts[reroller.reroll(SymbolType.THREE).ordinal()]++;
        }

        for (SymbolType symbol : SymbolType.values()) {
            if (symbol == SymbolType.THREE) {
                continue;
            }
            double ratio = counts[symbol.ordinal()] / (iterations / 5.0);
            assertTrue("Unexpected distribution for " + symbol, ratio > 0.85 && ratio < 1.15);
        }
    }
}
