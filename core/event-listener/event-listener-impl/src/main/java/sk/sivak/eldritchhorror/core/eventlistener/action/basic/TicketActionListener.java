package sk.sivak.eldritchhorror.core.eventlistener.action.basic;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import sk.sivak.eldritchhorror.core.constants.action.ActionButtonData;
import sk.sivak.eldritchhorror.core.constants.location.LocationInfo;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.action.AbstractActionPhaseAction;
import sk.sivak.eldritchhorror.core.eventlistener.action.AbstractActionPhaseListener;
import sk.sivak.eldritchhorror.core.model.InvestigatorRead;
import sk.sivak.eldritchhorror.core.model.LocationMapRead;

import static sk.sivak.eldritchhorror.core.constants.GameProperties.MAX_TRAVEL_TICKETS;
import static sk.sivak.eldritchhorror.core.constants.location.LocationType.CITY;

/**
 * @author msivak
 */
public class TicketActionListener extends AbstractActionPhaseListener<TicketActionListener.TicketAction> {

    private static final Logger logger = LogManager.getLogger(TicketActionListener.class);

    public TicketActionListener() {
        name = "Buy\nTicket";
    }

    private String additionalInfo;

    @Override
    protected String getAdditionalInfo() {
        isDisabled();
        return additionalInfo;
    }

    @Override
    protected String getGeneralDescription() {
        return "On a City space,\n" +
                "gain 1 Train or Ship ticket,\n" +
                "if City has that connection.";
    }

    @Override
    protected TicketAction createAction() {
        return new TicketAction();
    }

    @Override
    protected boolean isVisible() {
        return true;
    }

    @Override
    protected ActionButtonData.ActionButtonId getActionButtonId() {
        return ActionButtonData.ActionButtonId.TICKET;
    }

    @Override
    protected String getTexturePath() {
        return "action_button/ticket.png";
    }

    @Override
    protected boolean isDisabled() {
        LocationMapRead locationMap = ServicePlatform.get().getLocationMap();
        InvestigatorRead activeInvestigator = getActiveInvestigator();
        LocationInfo locationInfo = locationMap.getLocationInfo(activeInvestigator.getCurrentLocationId());

        if (CITY != locationInfo.getLocationType()) {
            disabledReason = "Can be only performed in City.";
            return true;
        }
        if (locationInfo.getShipConnections().size() + locationInfo.getTrainConnections().size() == 0) {
            disabledReason = "Space has no Train or Ship connections.";
            return true;
        }

        if (locationInfo.getShipConnections().size() > 0 && locationInfo.getTrainConnections().size() > 0) {
            additionalInfo =
                    "+1 Train ticket\n" +
                            "-or-\n" +
                            "+1 Ship ticket";
        } else if (locationInfo.getShipConnections().size() == 0) {
            additionalInfo = "+1 Train ticket";
        } else if (locationInfo.getTrainConnections().size() == 0) {
            additionalInfo = "+1 Ship ticket";
        }
        return false;
    }

    @Override
    protected boolean isNotRecommended() {
        InvestigatorRead activeInvestigator = getActiveInvestigator();
        if (activeInvestigator.getShipTickets() + activeInvestigator.getTrainTickets() >= MAX_TRAVEL_TICKETS) {
            notRecommendedReason = "You will have to discard one ticket.";
            return true;
        }
        return false;
    }

    protected class TicketAction extends AbstractActionPhaseAction {

        @Override
        public void execute() {
            logger.info("Executing buy travel ticket action...");
            ServicePlatform.get().getBasicActionService().buyTravelTicket();
        }
    }

}
