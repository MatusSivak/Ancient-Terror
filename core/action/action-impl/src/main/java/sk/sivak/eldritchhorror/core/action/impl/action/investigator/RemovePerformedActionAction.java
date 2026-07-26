package sk.sivak.eldritchhorror.core.action.impl.action.investigator;

import rx.SingleSubscriber;
import sk.sivak.eldritchhorror.core.action.AbstractHookableAction;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.constants.action.ActionPhaseAction;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;

/**
 * @author msivak
 */
public class RemovePerformedActionAction extends AbstractHookableAction<ActionPhaseAction, ActionPhaseAction> {

    public RemovePerformedActionAction() {
        super(BeforeAfterEvent.REMOVE_PERFORMED_ACTION);
    }

    @Override
    protected void onExecute(SingleSubscriber<? super ActionPhaseAction> ss) {
        InvestigatorId activeInvestigatorId = ServicePlatform.get().getInvestigators().getActiveInvestigatorId();
        ServicePlatform.get().getPerformedActions().removePerformedAction(activeInvestigatorId, input);
        ss.onSuccess(input);
    }
}
