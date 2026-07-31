package sk.sivak.eldritchhorror.core.action.provider;

import sk.sivak.eldritchhorror.core.action.Action;
import sk.sivak.eldritchhorror.core.action.impl.tutorial.AdjustCharterFlightAction;
import sk.sivak.eldritchhorror.core.action.impl.tutorial.AdjustRolledDicesInAcquireAssetsAction;
import sk.sivak.eldritchhorror.core.action.impl.tutorial.AdjustRolledDicesInCombatAction;
import sk.sivak.eldritchhorror.core.action.impl.tutorial.AdjustRolledDicesInMysteryEncounterAction;
import sk.sivak.eldritchhorror.core.action.impl.tutorial.AdjustRolledDicesInResearchAction;
import sk.sivak.eldritchhorror.core.action.impl.tutorial.ChangeCluePoolAction;
import sk.sivak.eldritchhorror.core.action.impl.tutorial.ChangeMonsterCupAction;
import sk.sivak.eldritchhorror.core.action.impl.tutorial.DisableWildernessEncounterAction;
import sk.sivak.eldritchhorror.core.action.impl.tutorial.DiscardManiacAction;
import sk.sivak.eldritchhorror.core.action.impl.tutorial.DisplayChalkboardAction;
import sk.sivak.eldritchhorror.core.action.impl.tutorial.DisplayTouchBlockersAction;
import sk.sivak.eldritchhorror.core.action.impl.tutorial.EnableOnlyThisActionAction;
import sk.sivak.eldritchhorror.core.action.impl.tutorial.EndTutorialAction;
import sk.sivak.eldritchhorror.core.action.impl.tutorial.HideChalkboardAction;
import sk.sivak.eldritchhorror.core.action.impl.tutorial.NotifyPhaseStartedAction;
import sk.sivak.eldritchhorror.core.action.impl.tutorial.ResetPerformedActionsAction;
import sk.sivak.eldritchhorror.core.action.impl.tutorial.ShowHudButtonsAction;
import sk.sivak.eldritchhorror.core.action.impl.tutorial.WaitForAncientOneClickAction;
import sk.sivak.eldritchhorror.core.action.impl.tutorial.WaitForAncientOneHideAction;
import sk.sivak.eldritchhorror.core.action.impl.tutorial.WaitForCharterFlightInspectedAction;
import sk.sivak.eldritchhorror.core.action.impl.tutorial.WaitForInvestigatorClickAction;
import sk.sivak.eldritchhorror.core.action.impl.tutorial.WaitForInvestigatorHideAction;
import sk.sivak.eldritchhorror.core.action.impl.tutorial.WaitForMysteryClickAction;
import sk.sivak.eldritchhorror.core.action.impl.tutorial.WaitForMysteryHideAction;
import sk.sivak.eldritchhorror.core.action.impl.tutorial.WaitForReserveClickAction;
import sk.sivak.eldritchhorror.core.constants.action.ActionButtonData;
import sk.sivak.eldritchhorror.core.eventtype.data.tutorial.CompositeTouchBlockerData;

public class TutorialActionProviderImpl implements TutorialActionProvider {

    @Override
    public Action<Object, Object> displayChalkboard(String text, int positionX, int positionY, boolean waitOnClick) {
        return new DisplayChalkboardAction(text, positionX, positionY, waitOnClick);
    }

    @Override
    public Action<Object, Object> hideChalkboard() {
        return new HideChalkboardAction();
    }

    @Override
    public Action<Object, Object> displayTouchBlockers(CompositeTouchBlockerData compositeTouchBlockerData) {
        return new DisplayTouchBlockersAction(compositeTouchBlockerData);
    }

    @Override
    public Action<Object, Object> showHudButtons() {
        return new ShowHudButtonsAction();
    }

    @Override
    public Action<Object, Object> waitForAncientOneClick() {
        return new WaitForAncientOneClickAction();
    }

    @Override
    public Action<Object, Object> waitForReserveClick() {
        return new WaitForReserveClickAction();
    }

    @Override
    public Action<Object, Object> waitForInvestigatorClick() {
        return new WaitForInvestigatorClickAction();
    }

    @Override
    public Action<Object, Object> waitForMysteryClick() {
        return new WaitForMysteryClickAction();
    }

    @Override
    public Action<Object, Object> waitForAncientOneHide() {
        return new WaitForAncientOneHideAction();
    }

    @Override
    public Action<Object, Object> waitForMysteryHide() {
        return new WaitForMysteryHideAction();
    }

    @Override
    public Action<Object, Object> waitForInvestigatorHide() {
        return new WaitForInvestigatorHideAction();
    }

    @Override
    public Action<Object, Object> enableOnlyThisAction(ActionButtonData.ActionButtonId actionButtonId) {
        return new EnableOnlyThisActionAction(actionButtonId);
    }

    @Override
    public Action<Object, Object> adjustRolledDicesInMysteryEncounter() {
        return new AdjustRolledDicesInMysteryEncounterAction();
    }

    @Override
    public Action<Object, Object> changeCluePool() {
        return new ChangeCluePoolAction();
    }

    @Override
    public Action<Object, Object> endTutorial(long startTime) {
        return new EndTutorialAction(startTime);
    }

    @Override
    public Action<Object, Object> notifyPhaseStarted(String phaseName) {
        return new NotifyPhaseStartedAction(phaseName);
    }

    @Override
    public Action<Object, Object> adjustRolledDicesInCombat() {
        return new AdjustRolledDicesInCombatAction();
    }

    @Override
    public Action<Object, Object> adjustRolledDicesInResearch() {
        return new AdjustRolledDicesInResearchAction();
    }

    @Override
    public Action<Object, Object> adjustRolledDicesInAcquireAssets() {
        return new AdjustRolledDicesInAcquireAssetsAction();
    }

    @Override
    public Action<Object, Object> changeMonsterCup() {
        return new ChangeMonsterCupAction();
    }

    @Override
    public Action<Object, Object> resetPerformedActions() {
        return new ResetPerformedActionsAction();
    }

    @Override
    public Action<Object, Object> disableWildernessEncounter() {
        return new DisableWildernessEncounterAction();
    }

    @Override
    public Action<Object, Object> waitForCharterFlightInspected() {
        return new WaitForCharterFlightInspectedAction();
    }

    @Override
    public Action<Object, Object> discardManiac() {
        return new DiscardManiacAction();
    }

    @Override
    public Action<Object, Object> adjustCharterFlight() {
        return new AdjustCharterFlightAction();
    }
}
