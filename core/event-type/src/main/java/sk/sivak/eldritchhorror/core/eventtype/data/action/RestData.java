package sk.sivak.eldritchhorror.core.eventtype.data.action;

/**
 * @author msivak
 */
public class RestData {
    private boolean healthEnabled = true;
    private boolean sanityEnabled = true;
    private int healthGained;
    private int sanityGained;

    public RestData(int healthGained, int sanityGained) {
        this.healthGained = healthGained;
        this.sanityGained = sanityGained;
    }

    public int getHealthGained() {
        return healthEnabled ? healthGained : 0;
    }

    public void setHealthGained(int healthGained) {
        this.healthGained = healthGained;
    }

    public int getSanityGained() {
        return sanityEnabled ? sanityGained : 0;
    }

    public void setSanityGained(int sanityGained) {
        this.sanityGained = sanityGained;
    }

    public void disableHealth() {
        healthEnabled = false;
    }

    public boolean isHealthEnabled() {
        return healthEnabled;
    }

    public void disableSanity() {
        sanityEnabled = false;
    }

    public boolean isSanityEnabled() {
        return sanityEnabled;
    }

    @Override
    public String toString() {
        return "RestData{" +
                "healthGained=" + healthGained +
                ", sanityGained=" + sanityGained +
                '}';
    }
}
