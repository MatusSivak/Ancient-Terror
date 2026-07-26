package sk.sivak.eldritchhorror.core.action.impl.action.general;

import rx.SingleSubscriber;
import sk.sivak.eldritchhorror.core.action.AbstractHookableAction;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.constants.location.PathType;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.model.InvestigatorWrite;

import static sk.sivak.eldritchhorror.core.constants.GameProperties.MAX_TRAVEL_TICKETS;

/**
 * @author msivak
 */
public class SelectTravelTicketAction extends AbstractHookableAction<Void, PathType> {

    public SelectTravelTicketAction() {
        super(BeforeAfterEvent.SELECT_TRAVEL_TICKET);
    }

    @Override
    protected void onExecute(SingleSubscriber<? super PathType> ss) {
        ServicePlatform.get().getGameController().selectTravelTicket().subscribe(pathType -> {
            InvestigatorWrite activeInvestigator = ServicePlatform.get().getInvestigators().getActiveInvestigator();
            checkTicketsLimit(activeInvestigator.getShipTickets(), activeInvestigator.getTrainTickets());
            ss.onSuccess(pathType);
        });

    }

    private void checkTicketsLimit(int shipTickets, int trainTickets) {
        if (shipTickets + trainTickets >= MAX_TRAVEL_TICKETS) {
            ServicePlatform.get().getBasicActionService().discardTicket();
        }
    }
}
