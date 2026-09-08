package sk.sivak.eldritchhorror.core.view.components.skilltestgrid;

public enum GridSuccessTarget {
    ONE(1, "1"),
    TWO(2, "2"),
    UNLIMITED(0, "Unlimited");

    private final int minimumSuccesses;
    private final String displayName;

    GridSuccessTarget(int minimumSuccesses, String displayName) {
        this.minimumSuccesses = minimumSuccesses;
        this.displayName = displayName;
    }

    /** Returns zero for Unlimited, which has no pass/fail threshold. */
    public int getMinimumSuccesses() {
        return minimumSuccesses;
    }

    public boolean isUnlimited() {
        return this == UNLIMITED;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
