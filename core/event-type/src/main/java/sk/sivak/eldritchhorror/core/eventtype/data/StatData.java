package sk.sivak.eldritchhorror.core.eventtype.data;

import sk.sivak.eldritchhorror.core.constants.investigator.Stat;

/**
 * @author msivak
 */
public class StatData {

    private Stat stat;
    private int baseValue;
    private int improvedValue;

    public Stat getStat() {
        return stat;
    }

    public void setStat(Stat stat) {
        this.stat = stat;
    }

    public int getBaseValue() {
        return baseValue;
    }

    public void setBaseValue(int baseValue) {
        this.baseValue = baseValue;
    }

    public int getImprovedValue() {
        return improvedValue;
    }

    public void setImprovedValue(int improvedValue) {
        this.improvedValue = improvedValue;
    }

    public int getTotalValue() {
        return improvedValue + baseValue;
    }
}
