package sk.sivak.eldritchhorror.core.action.impl.test;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import rx.SingleSubscriber;
import sk.sivak.eldritchhorror.core.action.AbstractHookableAction;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.TestRequestData;
import sk.sivak.eldritchhorror.core.eventtype.data.TestResponseData;

import java.util.LinkedList;

/**
 * @author msivak
 */
public class TestRequestToTestResponse extends AbstractHookableAction<TestRequestData, TestResponseData> {

    private static final Logger logger = LogManager.getLogger(TestRequestToTestResponse.class);

    public TestRequestToTestResponse() {
        super(BeforeAfterEvent.TEST_REQUEST_TO_TEST_RESPONSE);
    }

    @Override
    protected void onExecute(SingleSubscriber<? super TestResponseData> ss) {
        TestResponseData testResponseData = new TestResponseData();
        testResponseData.setRolledValues(new LinkedList<>());
        testResponseData.getRolledValues().add(1);
        testResponseData.getRolledValues().add(2);
        testResponseData.getRolledValues().add(4);
        ss.onSuccess(testResponseData);
    }
}
