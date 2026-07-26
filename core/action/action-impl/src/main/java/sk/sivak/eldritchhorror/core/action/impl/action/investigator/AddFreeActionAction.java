package sk.sivak.eldritchhorror.core.action.impl.action.investigator;

import rx.SingleSubscriber;
import sk.sivak.eldritchhorror.core.action.AbstractHookableAction;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;

import java.util.List;

/**
 * @author msivak
 */
public class AddFreeActionAction extends AbstractHookableAction<Object, Void> {

    public AddFreeActionAction() {
        super(BeforeAfterEvent.ADD_FREE_ACTION);
    }

    @Override
    protected void onExecute(SingleSubscriber<? super Void> ss) {
        InvestigatorId activeInvestigatorId = ServicePlatform.get().getInvestigators().getActiveInvestigatorId();
        ServicePlatform.get().getPerformedActions().addFreeAction(activeInvestigatorId);
        ss.onSuccess(null);
    }
}
