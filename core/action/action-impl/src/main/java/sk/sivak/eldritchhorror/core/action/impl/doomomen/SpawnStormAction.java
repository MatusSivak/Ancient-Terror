package sk.sivak.eldritchhorror.core.action.impl.doomomen;

import rx.Completable;
import rx.Single;
import sk.sivak.eldritchhorror.core.action.Action;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.constants.RumorCardInfo;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;

public class SpawnStormAction implements Action<Object, Void> {


    private final String stormId;
    private final LocationId location;

    public SpawnStormAction(String stormId, LocationId location) {
        this.stormId = stormId;
        this.location = location;
    }

    @Override
    public Single<Void> execute() {
        return Single.create(onSub -> {
            ServicePlatform.get().getGameController().spawnStorm(stormId, location).subscribe(() -> {
                onSub.onSuccess(null);
            });
        });
    }

    @Override
    public void setInput(Object input) {

    }
}
