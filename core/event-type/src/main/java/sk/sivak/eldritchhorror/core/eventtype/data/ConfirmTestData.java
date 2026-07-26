package sk.sivak.eldritchhorror.core.eventtype.data;

/**
 * @author msivak
 */
public class ConfirmTestData {

    private StatData statData;
    private int otherBonus;
    private int modifier;

    public StatData getStatData() {
        return statData;
    }

    public void setStatData(StatData statData) {
        this.statData = statData;
    }

    public int getModifier() {
        return modifier;
    }

    public void setModifier(int modifier) {
        this.modifier = modifier;
    }

    public int getOtherBonus() {
        return otherBonus;
    }

    public void setOtherBonus(int otherBonus) {
        this.otherBonus = otherBonus;
    }
}
