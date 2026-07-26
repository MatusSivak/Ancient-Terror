package sk.sivak.eldritchhorror.core.action.provider;

import sk.sivak.eldritchhorror.core.action.Action;
import sk.sivak.eldritchhorror.core.eventtype.data.tutorial.CompositeTouchBlockerData;

public interface TutorialActionProvider {
    Action<Object, Object> displayChalkboard(String text, int positionX, int positionY, boolean waitOnClick);

    Action<Object, Object> hideChalkboard();

    Action<Object, Object> displayTouchBlockers(CompositeTouchBlockerData compositeTouchBlockerData);

    Action<Object, Object> showHudButtons();

    Action<Object, Object> waitForAncientOneClick();

    Action<Object, Object> waitForReserveClick();

    Action<Object, Object> waitForMysteryClick();

    Action<Object, Object> waitForInvestigatorClick();

    Action<Object, Object> waitForAncientOneHide();

    Action<Object, Object> waitForMysteryHide();

    Action<Object, Object> waitForInvestigatorHide();

    Action<Object, Object> enableOnlyThisAction(String actionName);

    Action<Object, Object> adjustRolledDicesInCombat();

    Action<Object, Object> adjustRolledDicesInResearch();

    Action<Object, Object> adjustRolledDicesInAcquireAssets();

    Action<Object, Object> changeMonsterCup();

    Action<Object, Object> resetPerformedActions();

    Action<Object, Object> disableWildernessEncounter();

    Action<Object, Object> waitForCharterFlightInspected();

    Action<Object, Object> discardManiac();

    Action<Object, Object> adjustCharterFlight();

    Action<Object, Object> adjustRolledDicesInMysteryEncounter();

    Action<Object, Object> changeCluePool();

    Action<Object, Object> endTutorial(long startTime);

    Action<Object, Object> notifyPhaseStarted(String phaseName);
}
