package sk.sivak.eldritchhorror.core.eventlistener.action.character;

import sk.sivak.eldritchhorror.core.constants.action.ActionButtonData;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.action.AbstractActionPhaseAction;
import sk.sivak.eldritchhorror.core.eventlistener.action.AbstractActionPhaseListener;
import sk.sivak.eldritchhorror.core.model.InvestigatorRead;

public class TheReporterActionListener extends AbstractActionPhaseListener<TheReporterActionListener.CharacterAction> {
    public TheReporterActionListener() { name = "Sources"; }
    @Override protected String getAdditionalInfo() { return null; }
    @Override protected String getGeneralDescription() { return getActiveInvestigator().getInfo().getActionText(); }
    @Override protected CharacterAction createAction() { return new CharacterAction(); }
    @Override protected boolean isVisible() { return getInvestigators().getActiveInvestigatorId() == InvestigatorId.THE_REPORTER; }
    @Override protected boolean isDisabled() {
        if (ServicePlatform.get().getAssetDeck().getReserve().isEmpty()) {
            disabledReason = "Reserve is empty.";
            return true;
        }
        if (budget() < 1) {
            disabledReason = "Your Influence is too low.";
            return true;
        }
        return false;
    }
    @Override protected boolean isNotRecommended() { return false; }
    @Override protected ActionButtonData.ActionButtonId getActionButtonId() { return ActionButtonData.ActionButtonId.INVESTIGATOR; }
    @Override protected String getTexturePath() { return "investigator/THE_REPORTER.png"; }

    private int budget() {
        InvestigatorRead investigator = getActiveInvestigator();
        return investigator.getInfo().getBaseStat(Stat.INFLUENCE) + investigator.getStatBonus(Stat.INFLUENCE) - 1;
    }

    protected class CharacterAction extends AbstractActionPhaseAction {
        @Override public void execute() {
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getCardService().acquireAssets(budget());
            ServicePlatform.get().getService().convertToNull();
            ServicePlatform.get().getService().release();
        }
    }
}
