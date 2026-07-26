package sk.sivak.eldritchhorror.core.eventtype.data.basic;

/**
 * @author msivak
 */
public class GainSanityData {
    private int amount;

    public GainSanityData(int amount) {
        this.amount = amount;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}
