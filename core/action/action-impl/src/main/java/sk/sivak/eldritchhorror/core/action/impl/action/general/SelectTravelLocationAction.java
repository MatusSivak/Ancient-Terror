package sk.sivak.eldritchhorror.core.action.impl.action.general;

import rx.SingleSubscriber;
import sk.sivak.eldritchhorror.core.action.AbstractHookableAction;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.constants.location.LocationInfo;
import sk.sivak.eldritchhorror.core.controller.GameController;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.basic.TravelData;

/**
 * @author msivak
 */
public class SelectTravelLocationAction extends AbstractHookableAction<TravelData, LocationInfo.Connection> {

    public SelectTravelLocationAction() {
        super(BeforeAfterEvent.SELECT_TRAVEL_LOCATION);
    }

    @Override
    protected void onExecute(SingleSubscriber<? super LocationInfo.Connection> ss) {
        GameController gameController = ServicePlatform.get().getGameController();
        gameController.selectTravelLocation(input.isOptional(), input.getConnectionRestrictions()).subscribe(ss::onSuccess);
    }
}
