package sk.sivak.eldritchhorror.core.action.impl.investigator;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import rx.SingleSubscriber;
import sk.sivak.eldritchhorror.core.action.AbstractHookableAction;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionInfo;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.GainConditionFromDeckData;

/**
 * @author msivak
 */
public class GainConditionFromDeckAction extends AbstractHookableAction<GainConditionFromDeckData, GainConditionFromDeckData> {

    private static final Logger logger = LogManager.getLogger(GainConditionFromDeckAction.class);

    public GainConditionFromDeckAction() {
        super(BeforeAfterEvent.GAIN_CONDITION_FROM_DECK);
    }

    @Override
    protected void onExecute(SingleSubscriber<? super GainConditionFromDeckData> ss) {
        ConditionInfo conditionInfo = ServicePlatform.get().getConditionsDeck().gainCondition(input.getConditionId(), input.getInvestigatorId());
        ServicePlatform.get().getConditionListenerProvider().getConditionListener(conditionInfo).register(conditionInfo, input.getInvestigatorId());
        ss.onSuccess(input);
    }
}
