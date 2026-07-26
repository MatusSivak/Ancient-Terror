package sk.sivak.eldritchhorror.core.model;

import sk.sivak.eldritchhorror.core.constants.action.ActionPhaseAction;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;

import java.util.Set;

public interface PerformedActionsRead {
    int getActionsRemaining(InvestigatorId investigatorId);

    boolean canPerformAction(InvestigatorId investigatorId, ActionPhaseAction actionPhaseAction);

    boolean canPerformAction(InvestigatorId investigatorId);

    Set<ActionPhaseAction> getPerformedActions(InvestigatorId investigatorId);

    void clearFreeActions(InvestigatorId investigatorId);

    void setDontWantToPerformAction(InvestigatorId investigatorId);

    boolean wantsToPerformAction(InvestigatorId investigatorId);
}
