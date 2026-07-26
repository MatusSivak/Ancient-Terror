package sk.sivak.eldritchhorror.core.action.impl.test;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import rx.SingleSubscriber;
import sk.sivak.eldritchhorror.core.action.AbstractHookableAction;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.test.TestData;
import sk.sivak.eldritchhorror.core.model.InvestigatorWrite;

/**
 * @author msivak
 */
public class ReadStatAction extends AbstractHookableAction<TestData, TestData> {

    private static final Logger logger = LogManager.getLogger(ReadStatAction.class);

    public ReadStatAction() {
        super(BeforeAfterEvent.READ_STAT);
    }

    @Override
    protected void onExecute(SingleSubscriber<? super TestData> ss) {
        logger.debug("Reading stats for " + input.getStat());
        InvestigatorWrite activeInvestigator = ServicePlatform.get().getInvestigators().getActiveInvestigator();

        input.setBaseStatValue(activeInvestigator.getInfo().getBaseStat(input.getStat()));
        input.setBonusStatValue(activeInvestigator.getStatBonus(input.getStat()));
        ss.onSuccess(input);
    }
}
