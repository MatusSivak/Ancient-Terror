package sk.sivak.eldritchhorror.core.view.components.skilltestgrid;

import java.util.Random;

public final class GridTestParameters {
    private final int shifts;
    private final int swaps;
    private final int rerolls;
    private final int lifts;
    private final int superRerolls;
    private final int gaps;
    private final boolean momentum;
    private final boolean blind;
    private final TestMode difficulty;
    private final GridSuccessTarget successTarget;

    public GridTestParameters(int shifts, int swaps, int rerolls, int lifts, int superRerolls, int gaps,
                              boolean momentum, boolean blind, TestMode difficulty,
                              GridSuccessTarget successTarget) {
        validateRange("shifts", shifts, 1, 4);
        validateRange("swaps", swaps, 0, 4);
        validateRange("rerolls", rerolls, 0, 2);
        validateRange("lifts", lifts, 0, 1);
        validateRange("superRerolls", superRerolls, 0, 1);
        validateRange("gaps", gaps, 0, 3);
        if (difficulty == null) {
            throw new IllegalArgumentException("difficulty must not be null");
        }
        if (successTarget == null) {
            throw new IllegalArgumentException("successTarget must not be null");
        }
        this.shifts = shifts;
        this.swaps = swaps;
        this.rerolls = rerolls;
        this.lifts = lifts;
        this.superRerolls = superRerolls;
        this.gaps = gaps;
        this.momentum = momentum;
        this.blind = blind;
        this.difficulty = difficulty;
        this.successTarget = successTarget;
    }

    public static GridTestParameters randomized(Random random) {
        if (random == null) {
            throw new IllegalArgumentException("random must not be null");
        }
        return new GridTestParameters(1 + random.nextInt(4), random.nextInt(5), random.nextInt(3),
                random.nextInt(2), random.nextInt(2), random.nextInt(4),
                false, false, TestMode.NORMAL, GridSuccessTarget.ONE);
    }

    private static void validateRange(String name, int value, int minimum, int maximum) {
        if (value < minimum || value > maximum) {
            throw new IllegalArgumentException(name + " must be between " + minimum + " and " + maximum);
        }
    }

    public int getShifts() {
        return shifts;
    }

    public int getSwaps() {
        return swaps;
    }

    public int getRerolls() {
        return rerolls;
    }

    public int getLifts() {
        return lifts;
    }

    public int getSuperRerolls() {
        return superRerolls;
    }

    public int getGaps() {
        return gaps;
    }

    public boolean isMomentum() {
        return momentum;
    }

    public boolean isBlind() {
        return blind;
    }

    public TestMode getDifficulty() {
        return difficulty;
    }

    public GridSuccessTarget getSuccessTarget() {
        return successTarget;
    }
}
