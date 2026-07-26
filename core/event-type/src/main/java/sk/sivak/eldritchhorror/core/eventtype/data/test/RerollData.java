package sk.sivak.eldritchhorror.core.eventtype.data.test;

public class RerollData {
    private boolean executed;
    private TestData testData;

    public boolean isExecuted() {
        return executed;
    }

    public void setExecuted(boolean executed) {
        this.executed = executed;
    }

    public TestData getTestData() {
        return testData;
    }

    public void setTestData(TestData testData) {
        this.testData = testData;
    }
}
