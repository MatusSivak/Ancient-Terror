package sk.sivak.eldritchhorror.core.view.components.skilltestgrid;

import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.assertNotEquals;

public class FocusRerollerTest {

    @Test
    public void rerollGeneratesDifferentSymbol() {
        FocusReroller reroller = new FocusReroller(new Random(42));
        for (SymbolType symbol : SymbolType.values()) {
            for (int i = 0; i < 100; i++) {
                SymbolType rerolled = reroller.reroll(symbol);
                assertNotEquals("Reroll should differ from previous: " + symbol, symbol, rerolled);
            }
        }
    }

    @Test
    public void rerollFromOneExcludesOne() {
        FocusReroller reroller = new FocusReroller(new Random(42));
        for (int i = 0; i < 100; i++) {
            SymbolType rerolled = reroller.reroll(SymbolType.ONE);
            assertNotEquals(SymbolType.ONE, rerolled);
        }
    }

    @Test
    public void rerollFromSixExcludesSix() {
        FocusReroller reroller = new FocusReroller(new Random(42));
        for (int i = 0; i < 100; i++) {
            SymbolType rerolled = reroller.reroll(SymbolType.SIX);
            assertNotEquals(SymbolType.SIX, rerolled);
        }
    }

    @Test
    public void rerollDistributionIsUniform() {
        FocusReroller reroller = new FocusReroller(new Random(42));
        int[] counts = new int[6];
        SymbolType from = SymbolType.ONE;
        int iterations = 5000;

        for (int i = 0; i < iterations; i++) {
            SymbolType rerolled = reroller.reroll(from);
            counts[rerolled.ordinal()]++;
        }

        for (int i = 0; i < 6; i++) {
            if (SymbolType.values()[i] == from) {
                continue;
            }
            double expectedCount = iterations / 5.0;
            double actualCount = counts[i];
            double ratio = actualCount / expectedCount;
            assertTrue("Distribution should be roughly uniform for " + SymbolType.values()[i] + ": " + ratio,
                    ratio > 0.85 && ratio < 1.15);
        }
    }

    private void assertTrue(String message, boolean condition) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}
