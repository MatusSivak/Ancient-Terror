package sk.sivak.eldritchhorror.core.action.provider;

import sk.sivak.eldritchhorror.core.action.Action;
import sk.sivak.eldritchhorror.core.action.DirectEventAction;
import sk.sivak.eldritchhorror.core.action.HookableAction;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.monster.MonsterInfo;
import sk.sivak.eldritchhorror.core.eventtype.SelectMonsterData;
import sk.sivak.eldritchhorror.core.eventtype.data.encounter.AvailableEncounters;
import sk.sivak.eldritchhorror.core.eventtype.data.encounter.Encounter;
import sk.sivak.eldritchhorror.core.eventtype.data.encounter.EncounterResult;
import sk.sivak.eldritchhorror.core.eventtype.data.monster.DealDamageToMonsterData;
import sk.sivak.eldritchhorror.core.eventtype.data.monster.DefeatMonsterData;

public interface EncounterActionProvider {


    Action<Object, AvailableEncounters> collectEncounters();

    Action<AvailableEncounters, AvailableEncounters> buildEncounterButtons();

    HookableAction<AvailableEncounters,AvailableEncounters> disablePerformedEncounters();

    HookableAction<AvailableEncounters,AvailableEncounters> disableEpicCombatEncounters();

    HookableAction<AvailableEncounters,AvailableEncounters> disableNonCombatEncounters();

    DirectEventAction<AvailableEncounters> disableEncountersEvent();

    Action<AvailableEncounters, Encounter> selectEncounter();

    Action<Encounter, EncounterResult> executeEncounter();

    // Typewriter
    Action<Object, Void> showTypewriterPaper(boolean newPaper);

    Action<Object, Void> finishTypewriterPaper();

    Action<Object, Void> hideTypewriterPaper();

    Action<Object, Void> typeHeader(String header);

    Action<Object, Void> typeFlavor(String flavor);

    Action<Object, Void> typeInfo(String info);

    HookableAction<Object,Integer> displayButtons(String... buttonTexts);

    DirectEventAction<EncounterResult> investigatorFinishedEncounter();

    Action<Object, Void> disableAnotherEncounter(InvestigatorId investigatorId);

    Action<Object, Object> checkDefeatedBeforeShowingPaper();

    Action<Object, Object> checkDefeatedAfterEncounter();

    HookableAction<Object, Object> endOfEncounter();

    Action<Object, Object> insertDefeatSequencePoint();

    HookableAction<Object, String> readInput(String label);
}
