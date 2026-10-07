package sk.sivak.eldritchhorror.core.eventlistener.action.character;

import sk.sivak.eldritchhorror.core.constants.action.ActionButtonData;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.location.LocationInfo;
import sk.sivak.eldritchhorror.core.constants.location.PathType;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.action.AbstractActionPhaseAction;
import sk.sivak.eldritchhorror.core.eventlistener.action.AbstractActionPhaseListener;
import sk.sivak.eldritchhorror.core.eventtype.data.basic.TravelData;
import java.util.ArrayList;
import java.util.List;

public class TheExplorerActionListener extends AbstractActionPhaseListener<TheExplorerActionListener.CharacterAction> {
    public TheExplorerActionListener() { name = "Explore"; }
    @Override protected String getAdditionalInfo() { return null; }
    @Override protected String getGeneralDescription() { return "Move 1 space along an uncharted path, then perform 1 additional action."; }
    @Override protected CharacterAction createAction() { return new CharacterAction(); }
    @Override protected boolean isVisible() { return getInvestigators().getActiveInvestigatorId() == InvestigatorId.THE_EXPLORER; }
    @Override protected boolean isDisabled() {
        disabledReason = "There is no uncharted path nearby.";
        return connections().isEmpty();
    }
    @Override protected boolean isNotRecommended() { return false; }
    @Override protected ActionButtonData.ActionButtonId getActionButtonId() { return ActionButtonData.ActionButtonId.INVESTIGATOR; }
    @Override protected String getTexturePath() { return "investigator/THE_EXPLORER.png"; }
    protected class CharacterAction extends AbstractActionPhaseAction {
        @Override public void execute() {
            TravelData travel = new TravelData(false);
            travel.setConnectionRestrictions(connections());
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getBasicActionService().moveSpaces(travel, 1);
            ServicePlatform.get().getBasicActionService().addFreeAction();
            ServicePlatform.get().getGameService().justPerformAction();
            ServicePlatform.get().getService().release();
        }
    }
    private List<LocationInfo.Connection> connections() {
        List<LocationInfo.Connection> result = new ArrayList<>();
        for (LocationInfo.Connection connection : ServicePlatform.get().getLocationMap().getLocationInfo(getActiveInvestigator().getCurrentLocationId()).getConnections())
            if (connection.getPathType() == PathType.WALK) result.add(connection);
        return result;
    }
}

