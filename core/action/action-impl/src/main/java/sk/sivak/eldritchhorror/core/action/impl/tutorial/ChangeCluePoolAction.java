package sk.sivak.eldritchhorror.core.action.impl.tutorial;

import rx.Single;
import sk.sivak.eldritchhorror.core.action.Action;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;

public class ChangeCluePoolAction implements Action<Object, Object> {

    @Override
    public Single<Object> execute() {
        return Single.create(onSub -> {
            ServicePlatform.get().getCluePool().addOnTop(LocationId.SHANGHAI);
            ServicePlatform.get().getCluePool().addOnTop(LocationId.SPACE_3);
            onSub.onSuccess(null);
        });
    }

    @Override
    public void setInput(Object input) {

    }
}
