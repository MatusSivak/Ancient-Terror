package sk.sivak.eldritchhorror.core.action.impl.action;

import rx.Single;
import sk.sivak.eldritchhorror.core.action.Action;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;

public class HasAnyFreeActionAction implements Action<Object, Void> {
    @Override
    public Single<Void> execute() {
        return Single.create(onSub -> {
            InvestigatorId activeInvestigatorId = ServicePlatform.get().getInvestigators().getActiveInvestigatorId();
            boolean wantsToPerformAction = ServicePlatform.get().getPerformedActions().wantsToPerformAction(activeInvestigatorId);
            if (wantsToPerformAction) {
                onSub.onSuccess(null);
            } else {
                ServicePlatform.get().getService().skipBeforeEvent(BeforeAfterEvent.AFTER_ACTION_PERFORMED, null);
                onSub.onSuccess(null);
            }
        });
    }

    @Override
    public void setInput(Object input) {

    }
}
