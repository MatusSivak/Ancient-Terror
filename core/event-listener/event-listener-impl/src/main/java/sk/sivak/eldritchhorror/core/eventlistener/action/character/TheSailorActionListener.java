package sk.sivak.eldritchhorror.core.eventlistener.action.character;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import sk.sivak.eldritchhorror.core.constants.action.ActionButtonData;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.constants.location.LocationInfo;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.action.AbstractActionPhaseAction;
import sk.sivak.eldritchhorror.core.eventlistener.action.AbstractActionPhaseListener;
import sk.sivak.eldritchhorror.core.eventtype.data.basic.TravelData;

/**
 * @author msivak
 */
public class TheSailorActionListener extends AbstractActionPhaseListener<TheSailorActionListener.TheSailorAction> {

    private static final Logger logger = LogManager.getLogger(TheSailorActionListener.class);

    public TheSailorActionListener() {
        name = "Sail";
    }

    @Override
    protected String getAdditionalInfo() {
        return null;
    }

    @Override
    protected String getGeneralDescription() {
        return "Move 1 space\n" +
                "along a Ship path,\n" +
                "then perform\n" +
                "1 additional action.";
    }

    @Override
    protected TheSailorActionListener.TheSailorAction createAction() {
        return new TheSailorActionListener.TheSailorAction();
    }

    @Override
    protected ActionButtonData.ActionButtonId getActionButtonId() {
        return ActionButtonData.ActionButtonId.INVESTIGATOR;
    }

    @Override
    protected String getTexturePath() {
        return "investigator/ICON_THE_SAILOR.png";
    }

    @Override
    protected float getScaleDownPercentage() {
        return 0.75f;
    }

    @Override
    protected boolean isVisible() {
        return getInvestigators().getActiveInvestigatorId() == InvestigatorId.THE_SAILOR;
    }

    @Override
    protected boolean isDisabled() {
        LocationId currentLocationId = getActiveInvestigator().getCurrentLocationId();
        LocationInfo locationInfo = ServicePlatform.get().getLocationMap().getLocationInfo(currentLocationId);
        if (locationInfo.getShipConnections().size() <= 0) {
            disabledReason = "There is no Ship path nearby";
            return true;
        }
        return false;
    }

    @Override
    protected boolean isNotRecommended() {
        return false;
    }

    protected class TheSailorAction extends AbstractActionPhaseAction {

        @Override
        public void execute() {
            LocationId currentLocationId = getActiveInvestigator().getCurrentLocationId();
            LocationInfo locationInfo = ServicePlatform.get().getLocationMap().getLocationInfo(currentLocationId);
            TravelData travelData = new TravelData(false);
            travelData.setConnectionRestrictions(locationInfo.getShipConnections());

            logger.info("Executing the sailor action...");
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getBasicActionService().moveSpaces(travelData, 1);
            ServicePlatform.get().getBasicActionService().addFreeAction();
            ServicePlatform.get().getGameService().justPerformAction();
            ServicePlatform.get().getService().release();
        }
    }
}
