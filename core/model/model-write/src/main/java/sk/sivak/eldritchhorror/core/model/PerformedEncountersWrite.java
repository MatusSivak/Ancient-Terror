package sk.sivak.eldritchhorror.core.model;

import sk.sivak.eldritchhorror.core.constants.action.ActionPhaseAction;
import sk.sivak.eldritchhorror.core.constants.encounter.EncounterType;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;

import java.util.List;

public interface PerformedEncountersWrite extends PerformedEncountersRead {

    void perform(InvestigatorId investigatorId, EncounterType encounterType, String encounterId);

    void reset();

    void disableAnotherEncounter(InvestigatorId investigatorId);
}
