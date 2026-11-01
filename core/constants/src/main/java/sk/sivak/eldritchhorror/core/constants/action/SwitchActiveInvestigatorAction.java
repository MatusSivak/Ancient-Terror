package sk.sivak.eldritchhorror.core.constants.action;

import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;

/**
 * Pseudo action resolving pending action selection when the player switches to another investigator
 * during the action phase. It is not an investigator action and does not consume any action.
 */
public class SwitchActiveInvestigatorAction implements ActionPhaseAction {

    private final InvestigatorId investigatorId;

    public SwitchActiveInvestigatorAction(InvestigatorId investigatorId) {
        this.investigatorId = investigatorId;
    }

    public InvestigatorId getInvestigatorId() {
        return investigatorId;
    }

    @Override
    public ActionButtonData getActionButtonData() {
        return null;
    }

    @Override
    public String getName() {
        return "SwitchActiveInvestigator";
    }

    @Override
    public boolean isDisabled() {
        return false;
    }

    @Override
    public void execute() {
    }

    @Override
    public String getDisabledReason() {
        return null;
    }

    @Override
    public boolean isNotRecommended() {
        return false;
    }

    @Override
    public String getNotRecommendedReason() {
        return null;
    }

    @Override
    public String getGeneralDescription() {
        return null;
    }

    @Override
    public String getAdditionalInfo() {
        return null;
    }
}
