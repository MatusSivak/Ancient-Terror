package sk.sivak.eldritchhorror.core.action.impl.tutorial;

import rx.Single;
import sk.sivak.eldritchhorror.core.action.Action;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;

public class WaitForMysteryHideAction implements Action<Object, Object> {
    @Override
    public Single<Object> execute() {
        return Single.create(onSub -> {
            ServicePlatform.get().getInitGameController().waitForMysteryHide().subscribe(() -> {
                onSub.onSuccess(null);
            });
        });
    }

    @Override
    public void setInput(Object input) {

    }
}
