package sk.sivak.eldritchhorror.core.constants;

/** Temporary, process-only overrides enabled by local platform launchers. */
public final class LocalTesting {
    private static boolean allInvestigatorsUnlocked;
    private static boolean localPurchases;

    private LocalTesting() {}

    public static boolean useLocalPurchases() {
        return localPurchases;
    }

    public static void setLocalPurchases(boolean enabled) {
        localPurchases = enabled;
    }

    public static boolean areAllInvestigatorsUnlocked() {
        return allInvestigatorsUnlocked;
    }

    public static void setAllInvestigatorsUnlocked(boolean unlocked) {
        allInvestigatorsUnlocked = unlocked;
    }
}
