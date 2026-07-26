package sk.sivak.eldritchhorror.core.action.impl.tutorial;

import rx.Single;
import sk.sivak.eldritchhorror.core.action.Action;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;

public class ResetPerformedActionsAction implements Action<Object, Object> {


    @Override
    public Single<Object> execute() {
        return Single.create(onSub -> {
            ServicePlatform.get().getPerformedActions().reset();
            onSub.onSuccess(null);
        });
    }

    @Override
    public void setInput(Object input) {

    }
}
