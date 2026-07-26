package sk.sivak.eldritchhorror.core.action.impl.action.general;

import rx.SingleSubscriber;
import sk.sivak.eldritchhorror.core.action.AbstractHookableAction;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.constants.location.LocationInfo;
import sk.sivak.eldritchhorror.core.controller.GameController;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.model.InvestigatorWrite;

/**
 * @author msivak
 */
public class SelectTravelTicketLocationAction extends AbstractHookableAction<Object, LocationInfo.Connection> {

    public SelectTravelTicketLocationAction() {
        super(BeforeAfterEvent.SELECT_TRAVEL_TICKET_LOCATION);
    }

    @Override
    protected void onExecute(SingleSubscriber<? super LocationInfo.Connection> ss) {
        InvestigatorWrite activeInvestigator = ServicePlatform.get().getInvestigators().getActiveInvestigator();
        int shipTickets = activeInvestigator.getShipTickets();
        int trainTickets = activeInvestigator.getTrainTickets();

        if (shipTickets + trainTickets == 0) {
            ss.onSuccess(null);
        } else {
            GameController gameController = ServicePlatform.get().getGameController();
            gameController.selectTicketTravelLocation().subscribe(selectedLocationId -> {
                if (selectedLocationId == null) {
                    ss.onSuccess(null);
                } else {
                    ServicePlatform.get().getBasicActionService().hold();
                    ServicePlatform.get().getBasicActionService().loseTicket();
                    ServicePlatform.get().getBasicActionService().travelToLocation();
                    ServicePlatform.get().getBasicActionService().selectTravelTicketLocation();
                    ServicePlatform.get().getBasicActionService().release();

                    ss.onSuccess(selectedLocationId);
                }
            });
        }
    }
}
