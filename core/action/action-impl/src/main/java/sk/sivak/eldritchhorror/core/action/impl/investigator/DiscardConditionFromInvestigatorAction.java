package sk.sivak.eldritchhorror.core.action.impl.investigator;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import rx.SingleSubscriber;
import sk.sivak.eldritchhorror.core.action.AbstractHookableAction;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.DiscardConditionFromInvestigatorData;

/**
 * @author msivak
 */
public class DiscardConditionFromInvestigatorAction extends AbstractHookableAction<DiscardConditionFromInvestigatorData, DiscardConditionFromInvestigatorData> {

    private static final Logger logger = LogManager.getLogger(DiscardConditionFromInvestigatorAction.class);

    public DiscardConditionFromInvestigatorAction() {
        super(BeforeAfterEvent.DISCARD_CONDITION_FROM_INVESTIGATOR);
    }

    @Override
    protected void onExecute(SingleSubscriber<? super DiscardConditionFromInvestigatorData> ss) {
        ServicePlatform.get().getConditionsDeck().discard(input.getInvestigatorId(), input.getConditionInfo());
        ServicePlatform.get().getConditionListenerProvider().unregister(input.getConditionInfo());
        ss.onSuccess(input);
    }
}
