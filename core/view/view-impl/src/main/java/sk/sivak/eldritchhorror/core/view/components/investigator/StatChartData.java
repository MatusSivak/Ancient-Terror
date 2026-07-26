package sk.sivak.eldritchhorror.core.view.components.investigator;

public class StatChartData {
    private int health;
    private int sanity;
    private int will;
    private int lore;
    private int influence;
    private int observation;
    private int strength;

    public StatChartData(int health, int sanity, int will, int lore, int influence, int observation, int strength) {
        this.health = health;
        this.sanity = sanity;
        this.will = will;
        this.lore = lore;
        this.influence = influence;
        this.observation = observation;
        this.strength = strength;
    }

    public int getHealth() {
        return health;
    }

    public int getSanity() {
        return sanity;
    }

    public int getWill() {
        return will;
    }

    public int getLore() {
        return lore;
    }

    public int getInfluence() {
        return influence;
    }

    public int getObservation() {
        return observation;
    }

    public int getStrength() {
        return strength;
    }
}
