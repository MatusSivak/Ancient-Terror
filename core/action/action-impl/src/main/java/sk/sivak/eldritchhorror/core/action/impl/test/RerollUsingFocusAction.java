package sk.sivak.eldritchhorror.core.action.impl.test;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import rx.SingleSubscriber;
import rx.functions.Action1;
import sk.sivak.eldritchhorror.core.action.AbstractHookableAction;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.controller.TestController;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.test.CountRerollDiceData;
import sk.sivak.eldritchhorror.core.eventtype.data.test.RerollData;
import sk.sivak.eldritchhorror.core.eventtype.data.test.RerollUsingData;
import sk.sivak.eldritchhorror.core.eventtype.data.test.TestData;
import sk.sivak.eldritchhorror.core.eventtype.data.token.SpendData;

/**
 * @author msivak
 */
public class RerollUsingFocusAction extends AbstractHookableAction<RerollUsingData, RerollData> {

    private static final Logger logger = LogManager.getLogger(RerollUsingFocusAction.class);

    public RerollUsingFocusAction() {
        super(BeforeAfterEvent.REROLL_USING_FOCUS);
    }

    @Override
    protected void onExecute(SingleSubscriber<? super RerollData> ss) {
        RerollData rerollData = new RerollData();
        rerollData.setTestData(input.getTestData());
        if (input.getTestData().hasReachedMinScoreToEndTest()) {
            rerollData.setExecuted(false);
            ss.onSuccess(rerollData);
            return;
        }
        if (input.getTestData().getCalculatedDicePool() <= input.getTestData().getScore()) {
            rerollData.setExecuted(false);
            ss.onSuccess(rerollData);
            return;
        }

        TestController testController = ServicePlatform.get().getTestController();
        testController.askRerollUsingFocus(input.getQuestion()).subscribe(useFocusToken -> {
            if (useFocusToken) {
                ServicePlatform.get().getTokenService().spend(0, 1, 0, 0).subscribe(onSpend(rerollData));
                ss.onSuccess(null); // does not matter
            } else {
                rerollData.setExecuted(false);
                ss.onSuccess(rerollData);
            }
        });
    }

    private Action1<SpendData> onSpend(RerollData rerollData) {
        return spendData -> {
            if (spendData.hasEnough()) {
                rerollData.setExecuted(true);
                ServicePlatform.get().getTestService().hold();
                spendData.pay();
                ServicePlatform.get().getTestService().findCountRerollDice(CountRerollDiceData.CountRerollDiceType.FOCUS, input.getTestData());
                ServicePlatform.get().getTestService().rerollDice();
                ServicePlatform.get().getTestService().convertFromTo(TestData.class, RerollData.class, testData -> rerollData);
                ServicePlatform.get().getTestService().release();
            } else {
                rerollData.setExecuted(false);
                ServicePlatform.get().getTestService().convertTo(RerollData.class, () -> rerollData);
            }
        };
    }
}
