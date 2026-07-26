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
public class GainTravelTicketAction extends AbstractHookableAction<PathType, Void> {

    public GainTravelTicketAction() {
        super(BeforeAfterEvent.GAIN_TRAVEL_TICKET);
    }

    @Override
    protected void onExecute(SingleSubscriber<? super Void> ss) {
        InvestigatorWrite activeInvestigator = ServicePlatform.get().getInvestigators().getActiveInvestigator();
        int shipTickets = activeInvestigator.getShipTickets();
        int trainTickets = activeInvestigator.getTrainTickets();
        checkTicketsLimit(shipTickets, trainTickets);
    }

    private void checkTicketsLimit(int shipTickets, int trainTickets) {
        if (shipTickets + trainTickets + 1 > MAX_TRAVEL_TICKETS) {
            discardAndGainTicket();
        } else {
            gainTicket(shipTickets, trainTickets);
        }
    }

    private void discardAndGainTicket() {
        ServicePlatform.get().getService().hold();
        ServicePlatform.get().getBasicActionService().discardTicket(input);
        ServicePlatform.get().getBasicActionService().gainTravelTicket(input);
        ServicePlatform.get().getService().release();
        ss.onSuccess(null);
    }

    private void gainTicket(int shipTickets, int trainTickets) {
        InvestigatorWrite activeInvestigator = ServicePlatform.get().getInvestigators().getActiveInvestigator();
        if (input == PathType.TRAIN) {
            activeInvestigator.setTrainTickets(trainTickets + 1);
        } else if (input == PathType.SHIP) {
            activeInvestigator.setShipTickets(shipTickets + 1);
        }
        ServicePlatform.get().getGameController().gainTravelTicket(input).subscribe(() -> ss.onSuccess(null));
    }
}
