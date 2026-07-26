package sk.sivak.eldritchhorror.core.action.impl.game;

import rx.Single;
import rx.SingleSubscriber;
import sk.sivak.eldritchhorror.core.action.AbstractHookableAction;
import sk.sivak.eldritchhorror.core.action.Action;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.constants.clue.ClueInfo;
import sk.sivak.eldritchhorror.core.constants.location.Location;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;

public class SpawnClueAtAction implements Action<Object, Void> {

    private final LocationId locationId;

    public SpawnClueAtAction(LocationId locationId) {
        this.locationId = locationId;
    }

    @Override
    public Single<Void> execute() {
        return Single.create(onSub -> {
            ClueInfo clueInfo = ServicePlatform.get().getCluePool().spawnClue(locationId);
            ServicePlatform.get().getGameController()
                    .confirmClueLocation(clueInfo.getSpawnLocationId(), clueInfo.getCurrentLocationId()).subscribe(() -> {
                onSub.onSuccess(null);
            });
        });
    }

    @Override
    public void setInput(Object input) {

    }
}
