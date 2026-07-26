package sk.sivak.eldritchhorror.core.action.impl.game;

import rx.SingleSubscriber;
import sk.sivak.eldritchhorror.core.action.AbstractHookableAction;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;

public class DiscardClueAction extends AbstractHookableAction<LocationId, LocationId> {

    public DiscardClueAction() {
        super(BeforeAfterEvent.DISCARD_CLUE);
    }

    @Override
    protected void onExecute(SingleSubscriber<? super LocationId> ss) {
        LocationId spawnLocationId = ServicePlatform.get().getCluePool().discardClue(input);
        ServicePlatform.get().getGameController().discardClue(spawnLocationId, input).subscribe(() -> ss.onSuccess(input));
    }
}
