package sk.sivak.eldritchhorror.core.action.impl.game;

import rx.SingleSubscriber;
import sk.sivak.eldritchhorror.core.action.AbstractHookableAction;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.controller.GameController;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.basic.SelectLocationData;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class SelectLocationAction extends AbstractHookableAction<SelectLocationData, LocationId> {

    public SelectLocationAction() {
        super(BeforeAfterEvent.SELECT_LOCATION);
    }

    @Override
    protected void onExecute(SingleSubscriber<? super LocationId> ss) {
        GameController gameController = ServicePlatform.get().getGameController();
        List<LocationId> availableLocations;

        if (input.getLocations() == null) {
            availableLocations = Arrays.asList(LocationId.values());
        } else {
            availableLocations = input.getLocations();
        }
        if (availableLocations.size() == 1) {
            ss.onSuccess(availableLocations.get(0));
        } else {
            gameController.selectLocation(availableLocations).subscribe(v -> {
                gameController.showHud();
                ss.onSuccess(v.getLocationId());
            });
        }

    }
}
