package sk.sivak.eldritchhorror.core.action.impl.game;

import rx.Single;
import sk.sivak.eldritchhorror.core.action.Action;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.constants.clue.ClueInfo;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;

public class SpawnVortexAction implements Action<Object, Void> {

    private final LocationId locationId;

    public SpawnVortexAction(LocationId locationId) {
        this.locationId = locationId;
    }

    @Override
    public Single<Void> execute() {
        return Single.create(onSub -> {
            if (ServicePlatform.get().getVortexes().isAtLocation(locationId)) {
                onSub.onSuccess(null);
                return;
            }
            ServicePlatform.get().getVortexes().spawnVortex(locationId);
            ServicePlatform.get().getGameController().spawnVortex(locationId).subscribe(() -> {
                onSub.onSuccess(null);
            });
        });
    }

    @Override
    public void setInput(Object input) {

    }
}
