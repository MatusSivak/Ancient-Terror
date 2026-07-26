package sk.sivak.eldritchhorror.core.action.impl.action.investigator;

import rx.Single;
import sk.sivak.eldritchhorror.core.action.Action;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;

public class FindActiveInvestigatorAction implements Action<Object, InvestigatorId> {
    @Override
    public Single<InvestigatorId> execute() {
        return Single.create(onSub -> {
            onSub.onSuccess(ServicePlatform.get().getInvestigators().getActiveInvestigatorId());
        });
    }

    @Override
    public void setInput(Object input) {

    }
}
