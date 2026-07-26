package sk.sivak.eldritchhorror.core.action.impl.test;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import rx.SingleSubscriber;
import sk.sivak.eldritchhorror.core.action.AbstractHookableAction;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.test.CountRerollDiceData;

/**
 * @author msivak
 */
public class FindCountRerollDiceAction extends AbstractHookableAction<CountRerollDiceData, CountRerollDiceData> {

    private static final Logger logger = LogManager.getLogger(FindCountRerollDiceAction.class);

    public FindCountRerollDiceAction() {
        super(BeforeAfterEvent.COUNT_REROLL_DICE);
    }

    @Override
    protected void onExecute(SingleSubscriber<? super CountRerollDiceData> ss) {
        logger.info("Rerolling " + input.getCount() + " dice for type '" + input.getCountRerollDiceType() + "'");
        ss.onSuccess(input);
    }
}
