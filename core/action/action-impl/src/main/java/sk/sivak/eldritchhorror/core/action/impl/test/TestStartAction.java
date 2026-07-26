package sk.sivak.eldritchhorror.core.action.impl.test;

import rx.Single;
import sk.sivak.eldritchhorror.core.action.Action;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventtype.DirectEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.test.TestData;

/**
 * @author msivak
 */
public class TestStartAction implements Action<Object, TestData> {

    private TestData testData;

    public TestStartAction(TestData testData) {
        this.testData = testData;
    }

    @Override
    public Single<TestData> execute() {

        return Single.create(onSub -> {
            ServicePlatform.get().getEventQueue().fireDirectEvent(DirectEvent.TEST_START, testData);
            onSub.onSuccess(testData);
        });
    }

    @Override
    public void setInput(Object input) {
        //ignored
    }
}
