package sk.sivak.eldritchhorror.core.view.action;

import sk.sivak.eldritchhorror.core.view.game.InfoStage;

/**
 * Health / sanity tokens whose loss was already animated (one per monster fireball impact in combat).
 * The following model-driven loss consumes them instead of animating the same tokens again.
 */
public final class PrePlayedTokenLoss {

    private static int health;
    private static int sanity;

    private PrePlayedTokenLoss() {
    }

    public static synchronized void recordHealth() {
        health++;
    }

    public static synchronized void recordSanity() {
        sanity++;
    }

    /** @return how many of {@code amount} health tokens were already animated; the rest is cleared. */
    public static synchronized int consumeHealth(int amount) {
        int consumed = Math.min(health, amount);
        health = 0;
        return consumed;
    }

    /** @return how many of {@code amount} sanity tokens were already animated; the rest is cleared. */
    public static synchronized int consumeSanity(int amount) {
        int consumed = Math.min(sanity, amount);
        sanity = 0;
        return consumed;
    }

    /** Puts the already torn tokens back on the bars, so the real loss is animated normally later. */
    public static synchronized void undo() {
        if (InfoStage.getInvestigatorHud() != null) {
            for (int i = 0; i < health; i++) {
                InfoStage.getInvestigatorHud().getHealthBar().increaseCurrentValue();
            }
            for (int i = 0; i < sanity; i++) {
                InfoStage.getInvestigatorHud().getSanityBar().increaseCurrentValue();
            }
        }
        reset();
    }

    public static synchronized void reset() {
        health = 0;
        sanity = 0;
    }
}
