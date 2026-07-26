package sk.sivak.eldritchhorror.core.action.impl.test;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import rx.SingleSubscriber;
import sk.sivak.eldritchhorror.core.action.AbstractHookableAction;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.PrimitiveWrapper;

/**
 * @author msivak
 */
public class FindMinSuccessDiceValueAction extends AbstractHookableAction<PrimitiveWrapper<Integer>, PrimitiveWrapper<Integer>> {

    private static final Logger logger = LogManager.getLogger(FindMinSuccessDiceValueAction.class);

    public FindMinSuccessDiceValueAction() {
        super(BeforeAfterEvent.FIND_MIN_SUCCESS_DICE_VALUE);
    }

    @Override
    protected void onExecute(SingleSubscriber<? super PrimitiveWrapper<Integer>> ss) {
        logger.info("Min success dice value: " + input);
        ss.onSuccess(input);
    }
}
