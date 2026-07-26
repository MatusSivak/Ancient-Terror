package sk.sivak.eldritchhorror.core.action.impl.action.investigator;

import rx.Single;
import sk.sivak.eldritchhorror.core.action.Action;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;

public class UnsetActiveInvestigatorAction implements Action<Object, Void> {
    @Override
    public Single<Void> execute() {
        return Single.create(onSub -> {
            ServicePlatform.get().getInvestigators().unsetActiveInvestigator();
            onSub.onSuccess(null);
        });
    }

    @Override
    public void setInput(Object input) {

    }
}
