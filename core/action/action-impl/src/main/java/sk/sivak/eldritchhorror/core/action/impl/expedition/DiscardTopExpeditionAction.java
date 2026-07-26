package sk.sivak.eldritchhorror.core.action.impl.expedition;

import rx.Single;
import sk.sivak.eldritchhorror.core.action.Action;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;

public class DiscardTopExpeditionAction implements Action<Object, Void> {

    @Override
    public Single<Void> execute() {
        return Single.create(onSub -> {
            LocationId locationIdOld = ServicePlatform.get().getExpeditionDeck().getExpeditionTokenLocation();
            ServicePlatform.get().getExpeditionDeck().discardTopCard();
            LocationId locationIdNew = ServicePlatform.get().getExpeditionDeck().getExpeditionTokenLocation();

            if (locationIdOld.equals(locationIdNew)) {
                onSub.onSuccess(null);
            } else {
                ServicePlatform.get().getGameController().discardExpeditionToken().subscribe(() -> {
                    ServicePlatform.get().getService().hold();
                    ServicePlatform.get().getGameService().showExpeditionLocation().subscribe();
                    ServicePlatform.get().getGameService().convertToNull();
                    ServicePlatform.get().getService().release();
                    onSub.onSuccess(null);
                });
            }

        });
    }

    @Override
    public void setInput(Object input) {

    }
}
