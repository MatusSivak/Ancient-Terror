package sk.sivak.eldritchhorror.core.eventtype.data.test;

import rx.functions.Action1;

import java.util.LinkedList;
import java.util.List;

/**
 * @author msivak
 */
public class AddOneToDieResultData {

    private TestData testData;
    private List<Action1<TestData>> actions = new LinkedList<>();

    public AddOneToDieResultData(TestData testData) {
        this.testData = testData;
    }

    public TestData getTestData() {
        return testData;
    }

    public List<Action1<TestData>> getActions() {
        return actions;
    }

    public void setActions(List<Action1<TestData>> actions) {
        this.actions = actions;
    }
}
