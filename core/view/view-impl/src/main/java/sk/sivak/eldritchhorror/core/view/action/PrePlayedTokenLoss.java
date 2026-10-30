package sk.sivak.eldritchhorror.core.view.action;

import sk.sivak.eldritchhorror.core.view.components.hud.ContainerBar;

/** Tracks visual combat losses only while the original HUD contents are still present. */
public final class PrePlayedTokenLoss {
    private static final PendingLoss health = new PendingLoss();
    private static final PendingLoss sanity = new PendingLoss();

    private PrePlayedTokenLoss() { }

    public static synchronized void recordHealth(ContainerBar bar) { health.record(bar); }
    public static synchronized void recordSanity(ContainerBar bar) { sanity.record(bar); }
    public static synchronized int consumeHealth(int amount) { return health.consume(amount); }
    public static synchronized int consumeSanity(int amount) { return sanity.consume(amount); }

    /** Restore only tokens removed by this animation, never a refreshed investigator's HUD. */
    public static synchronized void undo() {
        health.undo();
        sanity.undo();
    }

    public static synchronized void reset() {
        health.clear();
        sanity.clear();
    }

    private static final class PendingLoss {
        private ContainerBar bar;
        private long generation;
        private int before, count;

        void record(ContainerBar target) {
            if (bar != target || !matches()) {
                clear();
                bar = target;
                generation = target.getContentsGeneration();
                before = target.getCurrentValue();
            }
            if (target.getCurrentValue() > 0) count++;
        }

        boolean matches() {
            return bar != null && bar.getContentsGeneration() == generation
                    && bar.getCurrentValue() == before - count;
        }

        int consume(int amount) {
            int consumed = matches() ? Math.min(count, Math.max(0, amount)) : 0;
            clear();
            return consumed;
        }

        void undo() {
            ContainerBar original = bar;
            int value = before;
            boolean restore = matches();
            clear();
            if (restore) original.setCurrentValue(value);
        }

        void clear() { bar = null; count = 0; before = 0; }
    }
}
