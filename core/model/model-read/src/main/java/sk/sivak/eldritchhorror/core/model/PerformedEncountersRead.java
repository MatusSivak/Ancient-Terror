package sk.sivak.eldritchhorror.core.model;

import sk.sivak.eldritchhorror.core.constants.action.ActionPhaseAction;
import sk.sivak.eldritchhorror.core.constants.encounter.EncounterType;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;

import java.util.List;
import java.util.Set;

public interface PerformedEncountersRead {

    List<EncounterType> getPerformedEncounters(InvestigatorId investigatorId);

    boolean isPerformed(InvestigatorId investigatorId, EncounterType encounterType, String encounterId);

    boolean hasEncounteredAnything(InvestigatorId investigatorId);

    boolean canHaveAnotherEncounter(InvestigatorId investigatorId);

    boolean canSkip(InvestigatorId investigatorId, boolean isAnyMonsterOnThisSpace);
}
