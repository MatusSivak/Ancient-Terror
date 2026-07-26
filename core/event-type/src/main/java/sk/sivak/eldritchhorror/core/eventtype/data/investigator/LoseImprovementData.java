package sk.sivak.eldritchhorror.core.eventtype.data.investigator;

import sk.sivak.eldritchhorror.core.constants.investigator.Stat;

public class LoseImprovementData {
    private Stat stat;
    private int amount;

    public LoseImprovementData(Stat stat, int amount) {
        this.stat = stat;
        this.amount = amount;
    }

    public Stat getStat() {
        return stat;
    }

    public void setStat(Stat stat) {
        this.stat = stat;
    }

    public int getAmount() {
        return amount;
    }
}
