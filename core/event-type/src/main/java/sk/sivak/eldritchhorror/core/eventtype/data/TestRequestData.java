package sk.sivak.eldritchhorror.core.eventtype.data;

import sk.sivak.eldritchhorror.core.constants.investigator.Stat;

/**
 * @author msivak
 */
public class TestRequestData {

    private Stat stat;
    private int modifier;
    private int baseValue;
    private int improvedValue;
    private TestAssets testAssetsData;

    public Stat getStat() {
        return stat;
    }

    public void setStat(Stat stat) {
        this.stat = stat;
    }

    public int getModifier() {
        return modifier;
    }

    public void setModifier(int modifier) {
        this.modifier = modifier;
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

    public TestAssets getTestAssetsData() {
        return testAssetsData;
    }

    public void setTestAssetsData(TestAssets testAssetsData) {
        this.testAssetsData = testAssetsData;
    }

    @Override
    public String toString() {
        return "TestRequestData{" +
                "stat=" + stat +
                ", modifier=" + modifier +
                ", baseValue=" + baseValue +
                ", improvedValue=" + improvedValue +
                ", dicePoolData=" + testAssetsData +
                '}';
    }
}
