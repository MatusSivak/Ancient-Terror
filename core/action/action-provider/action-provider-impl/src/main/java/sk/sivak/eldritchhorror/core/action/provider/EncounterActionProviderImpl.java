package sk.sivak.eldritchhorror.core.action.provider;

import sk.sivak.eldritchhorror.core.action.Action;
import sk.sivak.eldritchhorror.core.action.DirectEventAction;
import sk.sivak.eldritchhorror.core.action.HookableAction;
import sk.sivak.eldritchhorror.core.action.impl.action.investigator.CheckDefeatedAfterEncounterAction;
import sk.sivak.eldritchhorror.core.action.impl.action.investigator.CheckDefeatedBeforeShowingPaperAction;
import sk.sivak.eldritchhorror.core.action.impl.encounter.BuildEncounterButtonsAction;
import sk.sivak.eldritchhorror.core.action.impl.encounter.CollectEncountersAction;
import sk.sivak.eldritchhorror.core.action.impl.encounter.DisableEncountersEventAction;
import sk.sivak.eldritchhorror.core.action.impl.encounter.DisableEpicCombatEncountersAction;
import sk.sivak.eldritchhorror.core.action.impl.encounter.DisableNonCombatEncountersAction;
import sk.sivak.eldritchhorror.core.action.impl.encounter.DisablePerformedEncountersAction;
import sk.sivak.eldritchhorror.core.action.impl.encounter.EndOfEncounterAction;
import sk.sivak.eldritchhorror.core.action.impl.encounter.ExecuteEncounterAction;
import sk.sivak.eldritchhorror.core.action.impl.encounter.InsertDefeatSequencePointAction;
import sk.sivak.eldritchhorror.core.action.impl.encounter.InvestigatorFinishedEncounterAction;
import sk.sivak.eldritchhorror.core.action.impl.encounter.ReadInputAction;
import sk.sivak.eldritchhorror.core.action.impl.encounter.SelectEncounterAction;
import sk.sivak.eldritchhorror.core.action.impl.typewriter.DisableAnotherEncounterAction;
import sk.sivak.eldritchhorror.core.action.impl.typewriter.DisplayButtonsAction;
import sk.sivak.eldritchhorror.core.action.impl.typewriter.FinishTypewriterPaperAction;
import sk.sivak.eldritchhorror.core.action.impl.typewriter.HideTypewriterPaperAction;
import sk.sivak.eldritchhorror.core.action.impl.typewriter.ShowTypewriterPaperAction;
import sk.sivak.eldritchhorror.core.action.impl.typewriter.TypeFlavorAction;
import sk.sivak.eldritchhorror.core.action.impl.typewriter.TypeHeaderAction;
import sk.sivak.eldritchhorror.core.action.impl.typewriter.TypeInfoAction;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.eventtype.data.encounter.AvailableEncounters;
import sk.sivak.eldritchhorror.core.eventtype.data.encounter.Encounter;
import sk.sivak.eldritchhorror.core.eventtype.data.encounter.EncounterResult;

public class EncounterActionProviderImpl implements EncounterActionProvider {


    @Override
    public Action<Object, AvailableEncounters> collectEncounters() {
        return new CollectEncountersAction();
    }

    @Override
    public Action<AvailableEncounters, AvailableEncounters> buildEncounterButtons() {
        return new BuildEncounterButtonsAction();
    }

    @Override
    public HookableAction<AvailableEncounters, AvailableEncounters> disablePerformedEncounters() {
        return new DisablePerformedEncountersAction();
    }

    @Override
    public HookableAction<AvailableEncounters, AvailableEncounters> disableEpicCombatEncounters() {
        return new DisableEpicCombatEncountersAction();
    }

    @Override
    public HookableAction<AvailableEncounters, AvailableEncounters> disableNonCombatEncounters() {
        return new DisableNonCombatEncountersAction();
    }

    @Override
    public DirectEventAction<AvailableEncounters> disableEncountersEvent() {
        return new DisableEncountersEventAction();
    }

    @Override
    public Action<AvailableEncounters, Encounter> selectEncounter() {
        return new SelectEncounterAction();
    }

    @Override
    public Action<Encounter, EncounterResult> executeEncounter() {
        return new ExecuteEncounterAction();
    }

    //
    @Override
    public Action<Object, Void> showTypewriterPaper(boolean newPaper) {
        return new ShowTypewriterPaperAction(newPaper);
    }

    @Override
    public Action<Object, Void> finishTypewriterPaper() {
        return new FinishTypewriterPaperAction();
    }

    @Override
    public Action<Object, Void> hideTypewriterPaper() {
        return new HideTypewriterPaperAction();
    }

    @Override
    public Action<Object, Void> typeHeader(String header) {
        return new TypeHeaderAction(header);
    }

    @Override
    public Action<Object, Void> typeFlavor(String flavor) {
        return new TypeFlavorAction(flavor);
    }

    @Override
    public Action<Object, Void> typeInfo(String info) {
        return new TypeInfoAction(info);
    }

    @Override
    public HookableAction<Object, Integer> displayButtons(String... buttonTexts) {
        return new DisplayButtonsAction(buttonTexts);
    }

    @Override
    public DirectEventAction<EncounterResult> investigatorFinishedEncounter() {
        return new InvestigatorFinishedEncounterAction();
    }

    @Override
    public Action<Object, Void> disableAnotherEncounter(InvestigatorId investigatorId) {
        return new DisableAnotherEncounterAction(investigatorId);
    }

    @Override
    public Action<Object, Object> checkDefeatedBeforeShowingPaper() {
        return new CheckDefeatedBeforeShowingPaperAction();
    }


    @Override
    public Action<Object, Object> checkDefeatedAfterEncounter() {
        return new CheckDefeatedAfterEncounterAction();
    }

    @Override
    public HookableAction<Object, Object> endOfEncounter() {
        return new EndOfEncounterAction();
    }

    @Override
    public Action<Object, Object> insertDefeatSequencePoint() {
        return new InsertDefeatSequencePointAction();
    }

    @Override
    public HookableAction<Object, String> readInput(String label) {
        return new ReadInputAction(label);
    }
}
