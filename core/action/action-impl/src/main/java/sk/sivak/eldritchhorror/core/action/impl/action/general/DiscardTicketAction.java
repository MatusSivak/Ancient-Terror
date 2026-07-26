package sk.sivak.eldritchhorror.core.action.impl.action.general;

import rx.Completable;
import rx.SingleSubscriber;
import sk.sivak.eldritchhorror.core.action.AbstractHookableAction;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.constants.location.PathType;
import sk.sivak.eldritchhorror.core.controller.GameController;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.model.InvestigatorWrite;

/**
 * @author msivak
 */
public class DiscardTicketAction extends AbstractHookableAction<PathType, PathType> {

    public DiscardTicketAction() {
        super(BeforeAfterEvent.DISCARD_TRAVEL_TICKET);
    }

    @Override
    protected void onExecute(SingleSubscriber<? super PathType> ss) {
        InvestigatorWrite activeInvestigator = ServicePlatform.get().getInvestigators().getActiveInvestigator();
        GameController gameController = ServicePlatform.get().getGameController();
        Completable completable;
        int shipTickets = activeInvestigator.getShipTickets();
        int trainTickets = activeInvestigator.getTrainTickets();
        if (input == PathType.SHIP) {
            if (trainTickets > 0) {
                completable = gameController.discardTicket(PathType.TRAIN);
                activeInvestigator.setTrainTickets(trainTickets - 1);
            } else {
                completable = gameController.discardTicket(PathType.SHIP);
                activeInvestigator.setShipTickets(shipTickets - 1);
            }
        } else {
            if (shipTickets > 0) {
                completable = gameController.discardTicket(PathType.SHIP);
                activeInvestigator.setShipTickets(shipTickets - 1);
            } else {
                completable = gameController.discardTicket(PathType.TRAIN);
                activeInvestigator.setTrainTickets(trainTickets - 1);
            }
        }
        completable.subscribe(() -> ss.onSuccess(input));
    }
}
