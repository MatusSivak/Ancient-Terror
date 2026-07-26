package sk.sivak.eldritchhorror.core.service.data;

public class ClueFocusHealthSanity {
    private final int clue;
    private final int focus;
    private final int health;
    private final int sanity;

    public ClueFocusHealthSanity(int clue, int focus, int health, int sanity) {
        this.clue = clue;
        this.focus = focus;
        this.health = health;
        this.sanity = sanity;
    }

    public int getClue() {
        return clue;
    }

    public int getFocus() {
        return focus;
    }

    public int getHealth() {
        return health;
    }

    public int getSanity() {
        return sanity;
    }
}
