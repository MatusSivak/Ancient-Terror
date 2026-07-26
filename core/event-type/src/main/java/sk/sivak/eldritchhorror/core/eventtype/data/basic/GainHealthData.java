package sk.sivak.eldritchhorror.core.eventtype.data.basic;

/**
 * @author msivak
 */
public class GainHealthData {
    private int amount;

    public GainHealthData(int amount) {
        this.amount = amount;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}
