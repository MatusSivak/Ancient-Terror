package sk.sivak.eldritchhorror.core.action.impl.action.investigator;

import rx.SingleSubscriber;
import sk.sivak.eldritchhorror.core.action.AbstractHookableAction;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;

/**
 * @author msivak
 */
public class EndDelayedAction extends AbstractHookableAction<InvestigatorId, InvestigatorId> {

    public EndDelayedAction() {
        super(BeforeAfterEvent.END_DELAYED);
    }

    @Override
    protected void onExecute(SingleSubscriber<? super InvestigatorId> ss) {
        ServicePlatform.get().getInvestigators().getInvestigator(input).stopDelayed();
        ServicePlatform.get().getGameController().hideDelayedAnimation(input).subscribe(() ->
                ss.onSuccess(input)
        );

    }
}
