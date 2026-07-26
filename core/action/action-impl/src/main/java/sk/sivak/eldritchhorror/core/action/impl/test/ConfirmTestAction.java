package sk.sivak.eldritchhorror.core.action.impl.test;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import rx.SingleSubscriber;
import sk.sivak.eldritchhorror.core.action.AbstractHookableAction;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.constants.test.TestFlavorType;
import sk.sivak.eldritchhorror.core.constants.test.UsableAsset;
import sk.sivak.eldritchhorror.core.controller.TestController;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.test.TestData;

import java.util.LinkedList;
import java.util.List;

/**
 * @author msivak
 */
public class ConfirmTestAction extends AbstractHookableAction<TestData, TestData> {

    private static final Logger logger = LogManager.getLogger(ConfirmTestAction.class);

    public ConfirmTestAction() {
        super(BeforeAfterEvent.CONFIRM_TEST);
    }

    @Override
    protected void onExecute(SingleSubscriber<? super TestData> ss) {
        TestController testController = ServicePlatform.get().getTestController();
        testController.confirmTest(input.getStat(),
                input.getModifier(),
                input.getBaseStatValue(),
                input.getBonusStatValue(),
                input.getUsableAssets(),
                input.getAdditionalDicesCount(),
                input.isCombat())
                .subscribe(usableAssets -> doOnConfirm(usableAssets, ss));
    }

    private void doOnConfirm(List<UsableAsset> out, SingleSubscriber<? super TestData> ss) {
        input.setSelectedUsableAssets(out == null ? new LinkedList<>() : out);
        ss.onSuccess(input);
    }
}
