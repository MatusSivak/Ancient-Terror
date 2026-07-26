package sk.sivak.eldritchhorror.core.action.impl.action;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import rx.SingleSubscriber;
import sk.sivak.eldritchhorror.core.action.AbstractHookableAction;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.constants.action.ActionPhaseAction;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.eventlistener.action.AbstractActionPhaseAction;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.CollectAvailableActionsData;

/**
 * @author msivak
 */
public class FilterPerformedActionsAction extends AbstractHookableAction<CollectAvailableActionsData, CollectAvailableActionsData> {

    private static final Logger logger = LogManager.getLogger(FilterPerformedActionsAction.class);

    public FilterPerformedActionsAction() {
        super(BeforeAfterEvent.FILTER_PERFORMED_ACTIONS);
    }

    @Override
    protected void onExecute(SingleSubscriber<? super CollectAvailableActionsData> ss) {

        InvestigatorId activeInvestigatorId = ServicePlatform.get().getInvestigators().getActiveInvestigatorId();

        for (ActionPhaseAction actionPhaseAction : input.getActionPhaseActions()) {
            if (ServicePlatform.get().getPerformedActions().canPerformAction(activeInvestigatorId, actionPhaseAction)) {
                continue;
            }
            ((AbstractActionPhaseAction) actionPhaseAction).setDisabled(true);
            ((AbstractActionPhaseAction) actionPhaseAction).setDisabledReason("Action already performed this round");
        }

        ss.onSuccess(input);
    }
}
