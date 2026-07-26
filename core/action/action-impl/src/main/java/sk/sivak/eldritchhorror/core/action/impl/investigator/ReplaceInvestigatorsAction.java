package sk.sivak.eldritchhorror.core.action.impl.investigator;

import rx.SingleSubscriber;
import sk.sivak.eldritchhorror.core.action.AbstractHookableAction;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;

public class ReplaceInvestigatorsAction extends AbstractHookableAction<Object, Void> {

    public ReplaceInvestigatorsAction() {
        super(BeforeAfterEvent.REPLACE_INVESTIGATORS);
    }

    @Override
    protected void onExecute(SingleSubscriber<? super Void> ss) {
        ServicePlatform.get().getService().hold();
        for (InvestigatorId toBeReplacedInvestigator : ServicePlatform.get().getInvestigators().getToBeReplacedInvestigators()) {
            ServicePlatform.get().getInvestigatorService().selectReplacingInvestigator(toBeReplacedInvestigator);
        }
        ServicePlatform.get().getService().convertTo(Void.class, () -> null);
        ServicePlatform.get().getService().release();
        ss.onSuccess(null);
    }
}
