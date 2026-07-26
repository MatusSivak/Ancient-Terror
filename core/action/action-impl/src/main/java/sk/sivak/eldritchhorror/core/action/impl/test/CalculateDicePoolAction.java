package sk.sivak.eldritchhorror.core.action.impl.test;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import rx.SingleSubscriber;
import sk.sivak.eldritchhorror.core.action.AbstractHookableAction;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.test.TestData;

/**
 * @author msivak
 */
public class CalculateDicePoolAction extends AbstractHookableAction<TestData, TestData> {

    private static final Logger logger = LogManager.getLogger(CalculateDicePoolAction.class);

    public CalculateDicePoolAction() {
        super(BeforeAfterEvent.CALCULATE_DICE_POOL);
    }

    @Override
    protected void onExecute(SingleSubscriber<? super TestData> ss) {
        input.setCalculatedDicePool(input.calculateDicePool());
        logger.info("Calculated dice pool : " + input.getCalculatedDicePool());
        ss.onSuccess(input);
    }
}
