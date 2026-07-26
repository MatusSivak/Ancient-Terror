package sk.sivak.eldritchhorror.core.action.impl.game;

import rx.Completable;
import rx.SingleSubscriber;
import sk.sivak.eldritchhorror.core.action.AbstractHookableAction;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.token.MoveTokenData;

public class MoveClueAction extends AbstractHookableAction<MoveTokenData, MoveTokenData> {

    public MoveClueAction() {
        super(BeforeAfterEvent.MOVE_CLUE);
    }

    @Override
    protected void onExecute(SingleSubscriber<? super MoveTokenData> ss) {
        LocationId clueCurrentLocationId = ServicePlatform.get().getCluePool().getClueCurrentLocationId(input.getSpawnLocationId());
        Completable moveCameraToLocation = ServicePlatform.get().getGameController().moveCameraToLocation(clueCurrentLocationId);
        Completable moveClue = ServicePlatform.get().getGameController().moveClue(
                input.getSpawnLocationId(), clueCurrentLocationId, input.getTargetLocationId());

        moveCameraToLocation.subscribe(() -> moveClue.subscribe(() -> {
            ServicePlatform.get().getCluePool().updateClueLocation(input.getSpawnLocationId(), input.getTargetLocationId());
            ss.onSuccess(input);
        }));
    }
}
