package sk.sivak.eldritchhorror.core.action.impl.game;

import rx.Single;
import sk.sivak.eldritchhorror.core.action.Action;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;

public class RemoveVortexAction implements Action<Object, Void> {

    private final LocationId locationId;

    public RemoveVortexAction(LocationId locationId) {
        this.locationId = locationId;
    }

    @Override
    public Single<Void> execute() {
        return Single.create(onSub -> {
            ServicePlatform.get().getVortexes().removeVortex(locationId);
            ServicePlatform.get().getGameController().removeVortex(locationId).subscribe(() -> {
                onSub.onSuccess(null);
            });
        });
    }

    @Override
    public void setInput(Object input) {

    }
}
