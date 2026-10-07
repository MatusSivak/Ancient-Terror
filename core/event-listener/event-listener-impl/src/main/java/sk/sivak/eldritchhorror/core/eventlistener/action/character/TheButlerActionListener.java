package sk.sivak.eldritchhorror.core.eventlistener.action.character;

import sk.sivak.eldritchhorror.core.constants.action.ActionButtonData;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.action.AbstractActionPhaseAction;
import sk.sivak.eldritchhorror.core.eventlistener.action.AbstractActionPhaseListener;
import sk.sivak.eldritchhorror.core.model.InvestigatorRead;

public class TheButlerActionListener extends AbstractActionPhaseListener<TheButlerActionListener.CharacterAction> {
    public TheButlerActionListener() { name = "Assist"; }
    @Override protected String getAdditionalInfo() { return null; }
    @Override protected String getGeneralDescription() { return "Trade with another investigator on your space, then perform 1 additional action."; }
    @Override protected CharacterAction createAction() { return new CharacterAction(); }
    @Override protected boolean isVisible() { return getInvestigators().getActiveInvestigatorId() == InvestigatorId.THE_BUTLER; }
    @Override protected boolean isDisabled() {
        if (hasTraded()) {
            disabledReason = "You already performed a Trade action this round.";
            return true;
        }
        for (InvestigatorRead other : getInvestigators().getOnBoardInvestigators()) {
            if (other.getInfo().getInvestigatorId() != InvestigatorId.THE_BUTLER
                    && other.getCurrentLocationId() == getActiveInvestigator().getCurrentLocationId()) return false;
        }
        disabledReason = "No other investigator on this space.";
        return true;
    }
    @Override protected boolean isNotRecommended() { return false; }
    public static boolean hasTraded() {
        for (sk.sivak.eldritchhorror.core.constants.action.ActionPhaseAction performed :
                ServicePlatform.get().getPerformedActions().getPerformedActions(InvestigatorId.THE_BUTLER)) {
            if (performed instanceof TheButlerActionListener.CharacterAction
                    || performed.getActionButtonData().getActionButtonId() == ActionButtonData.ActionButtonId.TRADE) return true;
        }
        return false;
    }
    @Override protected ActionButtonData.ActionButtonId getActionButtonId() { return ActionButtonData.ActionButtonId.INVESTIGATOR; }
    @Override protected String getTexturePath() { return "investigator/THE_BUTLER.png"; }
    protected class CharacterAction extends AbstractActionPhaseAction {
        @Override public void execute() {
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getBasicActionService().trade();
            ServicePlatform.get().getBasicActionService().addFreeAction();
            ServicePlatform.get().getGameService().justPerformAction();
            ServicePlatform.get().getService().release();
        }
    }

}

