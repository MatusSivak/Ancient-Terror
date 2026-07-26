package sk.sivak.eldritchhorror.core.action.impl.test;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import rx.Completable;
import rx.SingleSubscriber;
import sk.sivak.eldritchhorror.core.action.AbstractHookableAction;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.test.TestData;

/**
 * @author msivak
 */
public class ShowRolledDicesAction extends AbstractHookableAction<TestData, TestData> {

    private static final Logger logger = LogManager.getLogger(ShowRolledDicesAction.class);

    public ShowRolledDicesAction() {
        super(BeforeAfterEvent.SHOW_ROLLED_DICES);
    }

    @Override
    protected void onExecute(SingleSubscriber<? super TestData> ss) {
        Completable completable = ServicePlatform.get().getTestController().showRolledTestDices(input.getDiceRolls());
        completable.subscribe(() -> {
            ss.onSuccess(input);
        });
    }
}
