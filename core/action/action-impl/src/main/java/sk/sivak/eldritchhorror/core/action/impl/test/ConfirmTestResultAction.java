package sk.sivak.eldritchhorror.core.action.impl.test;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import rx.Completable;
import rx.SingleSubscriber;
import sk.sivak.eldritchhorror.core.action.AbstractHookableAction;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.controller.TestController;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.test.TestData;

/**
 * @author msivak
 */
public class ConfirmTestResultAction extends AbstractHookableAction<TestData, TestData> {

    private static final Logger logger = LogManager.getLogger(ConfirmTestResultAction.class);

    public ConfirmTestResultAction() {
        super(BeforeAfterEvent.CONFIRM_TEST_RESULT);
    }

    @Override
    protected void onExecute(SingleSubscriber<? super TestData> ss) {
        TestController testController = ServicePlatform.get().getTestController();
        Completable completable = testController.confirmTestResult(input.getMinScoreToEndTest() > 1,
                input.isSuccessful(),
                input.getScore());
        completable.subscribe(() -> ss.onSuccess(input));
    }
}
