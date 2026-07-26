package sk.sivak.eldritchhorror.core.action.impl.expedition;

import rx.Single;
import sk.sivak.eldritchhorror.core.action.Action;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;

public class DestroyCurrentExpeditionLocationAction implements Action<Object, Void> {

    @Override
    public Single<Void> execute() {
        return Single.create(onSub -> {
            ServicePlatform.get().getExpeditionDeck().destroyCurrentExpeditionLocation();

            ServicePlatform.get().getGameController().discardExpeditionToken().subscribe(() -> {
                ServicePlatform.get().getService().hold();
                ServicePlatform.get().getGameService().convertToNull();
                ServicePlatform.get().getService().release();
                onSub.onSuccess(null);
            });

        });
    }

    @Override
    public void setInput(Object input) {

    }
}
