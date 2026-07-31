package sk.sivak.eldritchhorror.core.service;

import rx.Single;
import sk.sivak.eldritchhorror.core.action.Action;
import sk.sivak.eldritchhorror.core.action.provider.TutorialActionProvider;
import sk.sivak.eldritchhorror.core.constants.ViewProperties;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.constants.location.LocationInfo;
import sk.sivak.eldritchhorror.core.constants.location.PathType;
import sk.sivak.eldritchhorror.core.constants.monster.ManiacMonster;
import sk.sivak.eldritchhorror.core.constants.monster.MonsterInfo;
import sk.sivak.eldritchhorror.core.eventtype.data.tutorial.CompositeTouchBlockerData;
import sk.sivak.eldritchhorror.core.service.command.ActionCommand;

import java.util.concurrent.TimeUnit;

import static sk.sivak.eldritchhorror.core.constants.ViewProperties.MAP_HEIGHT;
import static sk.sivak.eldritchhorror.core.constants.ViewProperties.MAP_WIDTH;
import static sk.sivak.eldritchhorror.core.constants.ViewProperties.VIEWPORT_HEIGHT;
import static sk.sivak.eldritchhorror.core.constants.ViewProperties.VIEWPORT_WIDTH;

/**
 * @author msivak
 */
public class TutorialServiceImpl extends AbstractCommonService implements TutorialService {

    private static final int CENTER_X = ViewProperties.VIEWPORT_WIDTH / 2;
    private static final int CENTER_Y = ViewProperties.VIEWPORT_HEIGHT / 2;

    private BasicActionService basicActionService;
    private TokenService tokenService;
    private InvestigatorService investigatorService;
    private GameService gameService;
    private EncounterService encounterService;
    private Service service;
    private TutorialActionProvider tutorialActionProvider;
    private MonsterService monsterService;
    private DoomOmenService doomOmenService;


    public void setMonsterService(MonsterService monsterService) {
        this.monsterService = monsterService;
    }

    public void setDoomOmenService(DoomOmenService doomOmenService) {
        this.doomOmenService = doomOmenService;
    }

    public void setInvestigatorService(InvestigatorService investigatorService) {
        this.investigatorService = investigatorService;
    }

    public void setBasicActionService(BasicActionServiceImpl actionService) {
        this.basicActionService = actionService;
    }

    public void setTokenService(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    public void setEncounterService(EncounterService encounterService) {
        this.encounterService = encounterService;
    }

    public void setGameService(GameService gameService) {
        this.gameService = gameService;
    }

    public void setService(Service service) {
        this.service = service;
    }

    public void setTutorialActionProvider(TutorialActionProvider tutorialActionProvider) {
        this.tutorialActionProvider = tutorialActionProvider;
    }

    @Override
    public void startTutorial() {
        // TODO add listener -> reckonings must be successful
        hold();

        notifyPhaseStarted("Welcome");
        displayBlockerOnInfoStage();
        displayBlockerOnMapStage();
        displayChalkboard("tutorial.intro.welcome", CENTER_X, CENTER_Y, true);
        displayChalkboard("tutorial.intro.preventAncientOne", CENTER_X,CENTER_Y, true);
        showHudButtons();
        displayWindowOnInfoStage(5,395, 65,65,true);
        displayChalkboard("tutorial.intro.openAncientOneInfo", CENTER_X,CENTER_Y, false);
        waitForAncientOneClick();
        notifyPhaseStarted("Ancient One");
        hideChalkboard();
        displayWindowOnInfoStage(90,0,VIEWPORT_WIDTH-180, VIEWPORT_HEIGHT,false);
        waitForAncientOneHide();
        displayBlockerOnInfoStage();
        displayChalkboard("tutorial.ancientOne.azathothIntro", CENTER_X, CENTER_Y, true);
        displayChalkboard("tutorial.ancientOne.solveMysteries", CENTER_X, CENTER_Y, true);
        displayWindowOnInfoStage(5,260, 65,65,true);
        displayChalkboard("tutorial.ancientOne.viewMystery", CENTER_X, CENTER_Y, false);
        waitForMysteryClick();
        notifyPhaseStarted("Mystery");
        hideChalkboard();
        displayWindowOnInfoStage(190,100,600, 340,false);
        waitForMysteryHide();
        displayBlockerOnInfoStage();
        displayChalkboard("tutorial.mystery.tunguskaObjective", CENTER_X + 270, CENTER_Y + 130, true);
        displayChalkboard("tutorial.mystery.needClues", CENTER_X + 270, CENTER_Y + 130, true);
        hideChalkboard();
        investigatorService.changeActiveInvestigator();
        investigatorService.showActiveInvestigator(true);
        displayChalkboard("tutorial.investigator.checkSpecial", CENTER_X + 270, CENTER_Y - 130, false);
        displayWindowOnInfoStage(73,60,VIEWPORT_WIDTH-73, VIEWPORT_HEIGHT-60,false);
        displayWindowOnMapStage(MAP_WIDTH + 2171,MAP_HEIGHT-200,120, 120,true);
        waitForInvestigatorClick();
        notifyPhaseStarted("Investigator");
        displayBlockerOnMapStage();
        displayBlockerOnInfoStage();
        hideChalkboard();
        waitFewMilliseconds(2000);
        highlightSpyAction();
        waitForInvestigatorHide();
        displayBlockerOnInfoStage();
        displayChalkboard("tutorial.investigator.spyGainClue", CENTER_X, CENTER_Y, true);
        displayChalkboard("tutorial.actionPhase.start", CENTER_X, CENTER_Y, true);
        hideChalkboard();
        gameService.changeAndShowPhase();
        investigatorService.showActiveInvestigator(true);
        notifyPhaseStarted("Action Phase (1)");
        displayChalkboard("tutorial.actionPhase.firstAction", CENTER_X, CENTER_Y, true);
        hideChalkboard();
        displayWindowOnInfoStage(0,0,VIEWPORT_WIDTH, VIEWPORT_HEIGHT,false);
        displayWindowOnMapStage(-MAP_WIDTH,0,MAP_WIDTH*3, MAP_HEIGHT,false);
        enableOnlyThisAction("Gain\nClue");
        gameService.justPerformAction();
        displayChalkboard("tutorial.actionPhase.needSecondClue", CENTER_X, CENTER_Y, true);
        hideChalkboard();
        service.moveCameraToLocation(LocationId.THE_PYRAMIDS);
        displayChalkboard("tutorial.actionPhase.secondClueLocation", CENTER_X + 270, CENTER_Y, true);
        hideChalkboard();
        investigatorService.showActiveInvestigator(false);
        displayChalkboard("tutorial.actionPhase.secondActionTravel", CENTER_X + 270, CENTER_Y, true);
        displayChalkboard("tutorial.actionPhase.travelIstanbul", CENTER_X + 270, CENTER_Y, true);
        hideChalkboard();
        displayWindowOnMapStage(MAP_WIDTH + 1878 - 30, MAP_HEIGHT - 355 - 30, 60, 60, false);
        enableOnlyThisAction("Travel");
        gameService.justPerformAction();
        displayChalkboard("tutorial.actionPhase.noActionsLeft", CENTER_X, CENTER_Y, true);
        displayChalkboard("tutorial.actionPhase.encounterIncoming", CENTER_X, CENTER_Y, true);
        hideChalkboard();
        gameService.changeAndShowPhase();
        investigatorService.showActiveInvestigator(true);
        notifyPhaseStarted("Encounter Phase (1)");
        displayChalkboard("tutorial.encounterPhase.fightMonsters", CENTER_X + 270, CENTER_Y, true);
        displayChalkboard("tutorial.encounterPhase.prepareManiac", CENTER_X + 270, CENTER_Y, true);
        hideChalkboard();
        adjustRolledDicesInCombat();
        encounterService.justExecuteEncounter();
        displayChalkboard("tutorial.encounterPhase.goodJob", CENTER_X + 270, CENTER_Y, true);
        changeMonsterCup();
        hideChalkboard();
        encounterService.justExecuteEncounter();
        // OtherWorld -> Close Gate & Lose 2 Sanity
        // Istanbul -> Improved one Skill
        // General -> Leg Injury (reckoning: Leg Injury discarded, Illness gained)
        displayChalkboard("tutorial.encounterPhase.finished", CENTER_X, CENTER_Y, true);
        hideChalkboard();
        gameService.changeAndShowPhase();
        notifyPhaseStarted("Mythos Phase (1)");
        doomOmenService.drawMythosCard();
        doomOmenService.resolveCurrentMystery();
        displayChalkboard("tutorial.mythosPhase.survivedGateOpened", CENTER_X, CENTER_Y, true);
        displayChalkboard("tutorial.mythosPhase.newRound", CENTER_X, CENTER_Y, true);
        hideChalkboard();
        gameService.changeAndShowPhase();
        notifyPhaseStarted("Action Phase (2)");
        investigatorService.changeActiveInvestigator(InvestigatorId.THE_SPY);
        investigatorService.showActiveInvestigator(true);
        displayChalkboard("tutorial.actionPhase2.travelPyramids", CENTER_X + 270,CENTER_Y, true);
        hideChalkboard();
        displayWindowOnMapStage(MAP_WIDTH + 1765 - 30, MAP_HEIGHT - 509 - 30, 60, 60, false);
        enableOnlyThisAction("Travel");
        resetPerformedActions();
        gameService.justPerformAction();
        displayWindowOnMapStage(-MAP_WIDTH,0,MAP_WIDTH*3, MAP_HEIGHT,false);
        displayChalkboard("tutorial.actionPhase2.executeFocus", CENTER_X, CENTER_Y, true);
        hideChalkboard();
        enableOnlyThisAction("Focus");
        gameService.justPerformAction();
        displayChalkboard("tutorial.actionPhase2.focusToken", CENTER_X, CENTER_Y, true);
        hideChalkboard();
        gameService.changeAndShowPhase();
        investigatorService.showActiveInvestigator(true);
        notifyPhaseStarted("Encounter Phase (2)");
        displayChalkboard("tutorial.encounterPhase2.researchClue", CENTER_X + 270, CENTER_Y + 130, true);
        hideChalkboard();
        adjustRolledDicesInResearch();
        disableWildernessEncounter();
        encounterService.justExecuteEncounter();
        displayChalkboard("tutorial.encounterPhase2.extraClues", CENTER_X, CENTER_Y, true);
        hideChalkboard();
        changeCluePool();
        gameService.changeAndShowPhase();
        notifyPhaseStarted("Mythos Phase (2)");
        doomOmenService.drawMythosCard();
        doomOmenService.resolveCurrentMystery();
        gameService.changeAndShowPhase();
        investigatorService.changeActiveInvestigator(InvestigatorId.THE_SPY);
        investigatorService.showActiveInvestigator(true);
        notifyPhaseStarted("Action Phase (3)");
        displayChalkboard("tutorial.actionPhase3.finishMystery", CENTER_X + 270,CENTER_Y, true);
        hideChalkboard();
        displayWindowOnMapStage(MAP_WIDTH + 1878 - 30, MAP_HEIGHT - 355 - 30, 60, 60, false);
        enableOnlyThisAction("Travel");
        resetPerformedActions();
        gameService.justPerformAction();

        displayChalkboard("tutorial.actionPhase3.clickReserve", CENTER_X, CENTER_Y, false);
        displayWindowOnInfoStage(5,128, 65,65,true);
        displayBlockerOnMapStage();
        waitForReserveClick();
        displayBlockerOnInfoStage();
        hideChalkboard();
        waitFewMilliseconds(1500);
        displayWindowOnInfoStage(292, 121,181,250,true);
        waitForCharterFlightInspected();
        displayWindowOnInfoStage(0,0, VIEWPORT_WIDTH,VIEWPORT_HEIGHT,false);
        waitFewMilliseconds(500);
        displayChalkboard("tutorial.actionPhase3.obtainCharterFlight", CENTER_X, CENTER_Y, true);
        displayChalkboard("tutorial.actionPhase3.buyCharterFlight", CENTER_X, CENTER_Y, true);
        hideChalkboard();
        enableOnlyThisAction("Acquire\nAssets");
        adjustRolledDicesInAcquireAssets();
        adjustCharterFlight();
        displayWindowOnMapStage(MAP_WIDTH + 2231 - 30, MAP_HEIGHT - 311 - 30, 113 + 60, 131 + 60, false);
        gameService.justPerformAction();
        displayWindowOnMapStage(-MAP_WIDTH, 0, MAP_WIDTH * 3, MAP_HEIGHT, false);
        displayChalkboard("tutorial.actionPhase3.tunguskaArrival", CENTER_X, CENTER_Y, true);
        hideChalkboard();
        gameService.changeAndShowPhase();
        notifyPhaseStarted("Encounter Phase (3)");
        disableWildernessEncounter();
        adjustRolledDicesInMysteryEncounter();
        displayChalkboard("tutorial.encounterPhase3.useObservation", CENTER_X, CENTER_Y, true);
        hideChalkboard();
        encounterService.justExecuteEncounter();
        displayChalkboard("tutorial.encounterPhase3.discoveredMeteorite", CENTER_X, CENTER_Y, true);
        displayChalkboard("tutorial.encounterPhase3.mysteryResolvesNextMythos", CENTER_X, CENTER_Y, true);
        hideChalkboard();
        gameService.changeAndShowPhase();
        notifyPhaseStarted("Mythos Phase (3)");
        doomOmenService.drawMythosCard();
        doomOmenService.resolveCurrentMystery();
        displayChalkboard("tutorial.outro.congratulations", CENTER_X, CENTER_Y, true);
        displayChalkboard("tutorial.outro.readyQuestion", CENTER_X, CENTER_Y, true);
        hideChalkboard();
        endTutorial(System.currentTimeMillis());
        release();
    }

    private void endTutorial(long startTime) {
        execute(new ActionCommand<>(tutorialActionProvider.endTutorial(startTime), "END_TUTORIAL"));
    }

    private void notifyPhaseStarted(String phaseName) {
        execute(new ActionCommand<>(tutorialActionProvider.notifyPhaseStarted(phaseName), "NOTIFY_PHASE_STARTED"));
    }
    private void changeCluePool() {
        execute(new ActionCommand<>(tutorialActionProvider.changeCluePool(), "CHANGE_CLUE_POOL"));
    }

    private void adjustRolledDicesInMysteryEncounter() {
        execute(new ActionCommand<>(tutorialActionProvider.adjustRolledDicesInMysteryEncounter(), "ADJUST_ROLLED_DICES_IN_MYSTERY_ENCOUNTER"));
    }

    private void adjustCharterFlight() {
        execute(new ActionCommand<>(tutorialActionProvider.adjustCharterFlight(), "ADJUST_CHARTER_FLIGHT"));
    }

    private void discardManiac() {
        execute(new ActionCommand<>(tutorialActionProvider.discardManiac(), "DISCARD_MANIAC"));
    }

    private void waitForCharterFlightInspected() {
        execute(new ActionCommand<>(tutorialActionProvider.waitForCharterFlightInspected(), "WAIT_FOR_CHARTER_FLIGHT_INSPECTED"));
    }

    private void disableWildernessEncounter() {
        execute(new ActionCommand<>(tutorialActionProvider.disableWildernessEncounter(), "DISABLE_WILDERNESS_ENCOUNTER"));
    }

    private void resetPerformedActions() {
        execute(new ActionCommand<>(tutorialActionProvider.resetPerformedActions(), "RESET_PERFORMED_ACTIONS"));
    }

    private void changeMonsterCup() {
        execute(new ActionCommand<>(tutorialActionProvider.changeMonsterCup(), "CHANGE_MONSTER_CUP"));
    }

    private void adjustRolledDicesInCombat() {
        execute(new ActionCommand<>(tutorialActionProvider.adjustRolledDicesInCombat(), "ADJUST_ROLLED_DICES_IN_COMBAT"));
    }

    private void adjustRolledDicesInResearch() {
        execute(new ActionCommand<>(tutorialActionProvider.adjustRolledDicesInResearch(), "ADJUST_ROLLED_DICES_IN_RESEARCH"));
    }

    private void adjustRolledDicesInAcquireAssets() {
        execute(new ActionCommand<>(tutorialActionProvider.adjustRolledDicesInAcquireAssets(), "ADJUST_ROLLED_DICES_IN_ACQUIRE_ASSETS"));
    }

    private void enableOnlyThisAction(String actionName) {
        execute(new ActionCommand<>(tutorialActionProvider.enableOnlyThisAction(actionName), "ENABLE_ONLY_THIS_ACTION"));
    }

    private void highlightSpyAction() {
        CompositeTouchBlockerData.TouchBlockerData highlightSpyAction = displayWindowOnInfoStage(185, 95, 625, 55, true);
        highlightSpyAction.setClickthrough(true);
    }

    @Override
    public void highlightSpySpecial() {
        CompositeTouchBlockerData.TouchBlockerData highlightSpySpecial = displayWindowOnInfoStage(185, 39, 625, 55, true);
        highlightSpySpecial.setClickthrough(true);
    }


    private void waitForAncientOneHide() {
        execute(new ActionCommand<>(tutorialActionProvider.waitForAncientOneHide(), "WAIT_FOR_ANCIENT_ONE_HIDE"));
    }

    @Override
    public void waitForInvestigatorHide() {
        execute(new ActionCommand<>(tutorialActionProvider.waitForInvestigatorHide(), "WAIT_FOR_INVESTIGATOR_HIDE"));
    }

    private void waitForMysteryHide() {
        execute(new ActionCommand<>(tutorialActionProvider.waitForMysteryHide(), "WAIT_FOR_MYSTERY_HIDE"));
    }

    private void waitForAncientOneClick() {
        execute(new ActionCommand<>(tutorialActionProvider.waitForAncientOneClick(), "WAIT_FOR_ANCIENT_ONE_CLICK"));
    }

    private void waitForReserveClick() {
        execute(new ActionCommand<>(tutorialActionProvider.waitForReserveClick(), "WAIT_FOR_RESERVE_CLICK"));
    }

    @Override
    public void waitForInvestigatorClick() {
        execute(new ActionCommand<>(tutorialActionProvider.waitForInvestigatorClick(), "WAIT_FOR_INVESTIGATOR_CLICK"));
    }

    private void waitForMysteryClick() {
        execute(new ActionCommand<>(tutorialActionProvider.waitForMysteryClick(), "WAIT_FOR_MYSTERY_CLICK"));
    }

    private void showHudButtons() {
        execute(new ActionCommand<>(tutorialActionProvider.showHudButtons(), "SHOW_HUD_BUTTONS"));
    }

    @Override
    public void displayChalkboard(String text, int positionX, int positionY, boolean waitOnClick) {
        execute(new ActionCommand<>(tutorialActionProvider.displayChalkboard(text, positionX, positionY, waitOnClick), "DISPLAY_CHALKBOARD"));
    }

    @Override
    public void hideChalkboard() {
        execute(new ActionCommand<>(tutorialActionProvider.hideChalkboard(), "HIDE_CHALKBOARD"));
    }

    @Override
    public void displayTouchBlockers(CompositeTouchBlockerData compositeTouchBlockerData) {
        execute(new ActionCommand<>(tutorialActionProvider.displayTouchBlockers(compositeTouchBlockerData), "DISPLAY_TOUCH_BLOCKERS"));
    }

    private CompositeTouchBlockerData.TouchBlockerData createBlockerData(int x, int y, int width, int height) {
       return new CompositeTouchBlockerData.TouchBlockerData(new CompositeTouchBlockerData.Rectangle(
               x,y, width, height));
    }


    @Override
    public CompositeTouchBlockerData.TouchBlockerData displayWindowOnInfoStage(int windowX, int windowY, int windowWidth, int windowHeight, boolean semiTransparent) {
        return displayWindowOnStage(windowX, windowY, windowWidth, windowHeight, semiTransparent, CompositeTouchBlockerData.BlockerTarget.INFO_STAGE,
                0, 0,VIEWPORT_WIDTH, VIEWPORT_HEIGHT);
    }

    private CompositeTouchBlockerData.TouchBlockerData displayWindowOnMapStage(int windowX, int windowY, int windowWidth, int windowHeight, boolean semiTransparent) {
        return displayWindowOnStage(windowX, windowY, windowWidth, windowHeight, semiTransparent, CompositeTouchBlockerData.BlockerTarget.MAP,
                -MAP_WIDTH,0, MAP_WIDTH*3, MAP_HEIGHT);
    }

    private CompositeTouchBlockerData.TouchBlockerData displayWindowOnStage(int windowX, int windowY, int windowWidth, int windowHeight,
                                      boolean semiTransparent, CompositeTouchBlockerData.BlockerTarget blockerTarget,
                                      int x, int y, int width, int height) {
        CompositeTouchBlockerData compositeTouchBlockerData = new CompositeTouchBlockerData(blockerTarget);
        CompositeTouchBlockerData.TouchBlockerData touchBlockerData = createBlockerData(x,y, width, height);
        touchBlockerData.setSemiTransparent(semiTransparent);
        touchBlockerData.setWindowArea(new CompositeTouchBlockerData.Rectangle(windowX,windowY,windowWidth,windowHeight));
        compositeTouchBlockerData.addTouchBlockerData(touchBlockerData);
        displayTouchBlockers(compositeTouchBlockerData);
        return touchBlockerData;
    }

    @Override
    public void displayBlockerOnInfoStage() {
        CompositeTouchBlockerData compositeTouchBlockerData = new CompositeTouchBlockerData(CompositeTouchBlockerData.BlockerTarget.INFO_STAGE);
        CompositeTouchBlockerData.TouchBlockerData touchBlockerData = createBlockerData(0,0, VIEWPORT_WIDTH, VIEWPORT_HEIGHT);
        compositeTouchBlockerData.addTouchBlockerData(touchBlockerData);
        displayTouchBlockers(compositeTouchBlockerData);
    }

    @Override
    public void displayBlockerOnMapStage() {
        CompositeTouchBlockerData compositeTouchBlockerData = new CompositeTouchBlockerData(CompositeTouchBlockerData.BlockerTarget.MAP);
        CompositeTouchBlockerData.TouchBlockerData touchBlockerData = createBlockerData(-MAP_WIDTH,0, MAP_WIDTH*3, MAP_HEIGHT);
        compositeTouchBlockerData.addTouchBlockerData(touchBlockerData);
        displayTouchBlockers(compositeTouchBlockerData);
    }

    @Override
    public void waitFewMilliseconds(int milliseconds) {
        execute(new ActionCommand<>(new Action<Object, Object>() {
            @Override
            public Single<Object> execute() {
                return Single.just((Object)"OK").delay(milliseconds, TimeUnit.MILLISECONDS);
            }

            @Override
            public void setInput(Object input) {

            }
        }, "Waiting few milliseconds"));
    }
}
