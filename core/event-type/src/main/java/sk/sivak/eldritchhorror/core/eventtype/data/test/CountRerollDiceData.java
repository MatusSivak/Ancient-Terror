package sk.sivak.eldritchhorror.core.eventtype.data.test;

import java.util.LinkedList;
import java.util.List;

public class CountRerollDiceData {
    private TestData testData;
    private CountRerollDiceType countRerollDiceType;
    private List<Integer> rerolledValues = new LinkedList<>();
    private int count;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public TestData getTestData() {
        return testData;
    }

    public void setTestData(TestData testData) {
        this.testData = testData;
    }

    public CountRerollDiceType getCountRerollDiceType() {
        return countRerollDiceType;
    }

    public void setCountRerollDiceType(CountRerollDiceType countRerollDiceType) {
        this.countRerollDiceType = countRerollDiceType;
    }

    public List<Integer> getRerolledValues() {
        return rerolledValues;
    }

    public void addRerolledValue(Integer rerolledValue) {
        rerolledValues.add(rerolledValue);
    }

    public enum CountRerollDiceType {
        CLUE,
        FOCUS,
        OTHER
    }
}
