package sk.sivak.eldritchhorror.core.action.impl.action.general;

import rx.SingleSubscriber;
import sk.sivak.eldritchhorror.core.action.AbstractHookableAction;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.constants.location.LocationInfo;
import sk.sivak.eldritchhorror.core.constants.location.PathType;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.model.InvestigatorWrite;

/**
 * @author msivak
 */
public class LoseTicketAction extends AbstractHookableAction<LocationInfo.Connection, LocationInfo.Connection> {

    public LoseTicketAction() {
        super(BeforeAfterEvent.LOSE_TICKET);
    }

    @Override
    protected void onExecute(SingleSubscriber<? super LocationInfo.Connection> ss) {
        ServicePlatform.get().getGameController().loseTicket(input.getPathType());
        InvestigatorWrite activeInvestigator = ServicePlatform.get().getInvestigators().getActiveInvestigator();
        if (input.getPathType() == PathType.SHIP) {
            activeInvestigator.setShipTickets(activeInvestigator.getShipTickets() - 1);
        } else if (input.getPathType() == PathType.TRAIN) {
            activeInvestigator.setTrainTickets(activeInvestigator.getTrainTickets() - 1);
        }
        ss.onSuccess(input);
    }
}
