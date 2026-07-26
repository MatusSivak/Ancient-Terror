package sk.sivak.eldritchhorror.core.model;

import sk.sivak.eldritchhorror.core.constants.action.ActionPhaseAction;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;

public interface PerformedActionsWrite extends PerformedActionsRead {
    void init(InvestigatorId... investigatorIds);

    void clearPerformedActionsMap();

    void reset();

    void registerActionPerformed(InvestigatorId investigatorId, ActionPhaseAction actionPhaseAction);

    void addFreeAction(InvestigatorId investigatorId);

    void clearFreeActions(InvestigatorId investigatorId);

    void removePerformedAction(InvestigatorId investigatorId, ActionPhaseAction actionPhaseAction);
}
