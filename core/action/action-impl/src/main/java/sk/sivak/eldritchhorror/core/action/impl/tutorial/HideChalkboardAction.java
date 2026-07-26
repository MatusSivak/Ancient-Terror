package sk.sivak.eldritchhorror.core.action.impl.tutorial;

import rx.Single;
import sk.sivak.eldritchhorror.core.action.Action;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;

public class HideChalkboardAction implements Action<Object, Object> {

    public HideChalkboardAction() {
    }

    @Override
    public Single<Object> execute() {
        return Single.create(onSub -> {
            ServicePlatform.get().getInitGameController().hideChalkboard();
            onSub.onSuccess("whatever");
        });
    }

    @Override
    public void setInput(Object input) {

    }
}
