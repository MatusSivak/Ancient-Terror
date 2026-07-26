package sk.sivak.eldritchhorror.core.service;

import rx.Single;
import sk.sivak.eldritchhorror.core.action.Action;
import sk.sivak.eldritchhorror.core.action.provider.EncounterActionProvider;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.eventtype.data.encounter.Encounter;
import sk.sivak.eldritchhorror.core.eventtype.data.encounter.EncounterResult;
import sk.sivak.eldritchhorror.core.service.command.ActionCommand;

public class EncounterServiceImpl extends AbstractCommonService implements EncounterService {

    private EncounterActionProvider encounterActionProvider;

    private InvestigatorService investigatorService;

    private GameService gameService;

    public void setEncounterActionProvider(EncounterActionProvider encounterActionProvider) {
        this.encounterActionProvider = encounterActionProvider;
    }

    public void setInvestigatorService(InvestigatorService investigatorService) {
        this.investigatorService = investigatorService;
    }

    public void setGameService(GameService gameService) {
        this.gameService = gameService;
    }

    @Override
    public void encounter() {
        hold();
        justExecuteEncounter();
        execute(new ActionCommand<>(new Action<EncounterResult, Void>() {
            private EncounterResult encounterResult;

            @Override
            public Single<Void> execute() {
                return Single.create(onSub -> {
                    hold();
                    if (!encounterResult.canHaveAnotherEncounter()) {
                        investigatorFinishedEncounter();
                        if (!encounterResult.isLast()) {
                            investigatorService.changeAndShowActiveInvestigator();
                            encounter();
                        } else {
                            investigatorService.changeActiveInvestigator();
                            gameService.changeAndShowPhase();
                            gameService.initPhase();
                        }
                    } else {
                        hold();
                        investigatorService.showActiveInvestigator(false);
                        encounter();
                        release();
                    }

                    release();
                    onSub.onSuccess(null);
                });
            }

            @Override
            public void setInput(EncounterResult input) {
                this.encounterResult = input;
            }

        }, "NEW_ROUND"));
        release();
    }

    @Override
    public void justExecuteEncounter() {
        hold();
        execute(new ActionCommand<>(encounterActionProvider.collectEncounters(), "COLLECT_ENCOUNTERS"));
        execute(new ActionCommand<>(encounterActionProvider.buildEncounterButtons(), "BUILD_ENCOUNTER_BUTTONS"));
        disableEncounters();
        execute(new ActionCommand<>(encounterActionProvider.selectEncounter(), "SELECT_ENCOUNTER"));
        insertDefeatSequencePoint();
        executeEncounter();
        release();
    }

    @Override
    public void executeEncounter() {
        execute(new ActionCommand<>(encounterActionProvider.executeEncounter(), "EXECUTE_ENCOUNTER"));
    }


    private void investigatorFinishedEncounter() {
        execute(new ActionCommand<>(encounterActionProvider.investigatorFinishedEncounter(), "INVESTIGATOR_FINISHED_ENCOUNTER"));
    }

    private void disableEncounters() {
        executeHookable(encounterActionProvider.disablePerformedEncounters());
        executeHookable(encounterActionProvider.disableEpicCombatEncounters());
        executeHookable(encounterActionProvider.disableNonCombatEncounters());
        execute(new ActionCommand<>(encounterActionProvider.disableEncountersEvent(), "DISABLE_ENCOUNTERS_EVENT"));
    }

    // Typewriter things...
    @Override
    public void showTypewriterPaper(boolean newPaper) {
        execute(new ActionCommand<>(encounterActionProvider.checkDefeatedBeforeShowingPaper(), "CHECK_DEFEATED_BEFORE_SHOWING_PAPER"));
        execute(new ActionCommand<>(encounterActionProvider.showTypewriterPaper(newPaper), "TW_SHOW_TYPEWRITER_PAPER"));
    }

    @Override
    public void finishTypewriterPaper() {
        execute(new ActionCommand<>(encounterActionProvider.finishTypewriterPaper(), "TW_FINISH_TYPEWRITER_PAPER"));
    }



    @Override
    public void hideTypewriterPaper() {
        execute(new ActionCommand<>(encounterActionProvider.hideTypewriterPaper(), "TW_HIDE_TYPEWRITER_PAPER"));
    }

    @Override
    public void typeHeader(String header) {
        execute(new ActionCommand<>(encounterActionProvider.typeHeader(header), "TW_TYPE_HEADER"));
    }

    @Override
    public void typeFlavor(String flavor) {
        execute(new ActionCommand<>(encounterActionProvider.typeFlavor(flavor), "TW_TYPE_FLAVOR"));
    }

    @Override
    public void typeInfo(String info) {
        execute(new ActionCommand<>(encounterActionProvider.typeInfo(info), "TW_TYPE_INFO"));
    }

    @Override
    public Single<Integer> displayButtons(String... buttonTexts) {
        return Single.create(onSub -> {
            executeHookable(encounterActionProvider.displayButtons(buttonTexts), onSub);
        });
    }

    @Override
    public void disableAnotherEncounter(InvestigatorId investigatorId) {
        execute(new ActionCommand<>(encounterActionProvider.disableAnotherEncounter(investigatorId), "DISABLE_ANOTHER_ENCOUNTER"));
    }

    @Override
    public void checkDefeatedAfterEncounter() {
        execute(new ActionCommand<>(encounterActionProvider.checkDefeatedAfterEncounter(), "CHECK_DEFEATED_AFTER_ENCOUNTER"));
    }


    @Override
    public void endOfEncounter(Encounter encounter) {
        executeHookable(encounter, encounterActionProvider.endOfEncounter());
    }

    @Override
    public void endOfEncounter() {
        executeHookable(encounterActionProvider.endOfEncounter());
    }

    @Override
    public void insertDefeatSequencePoint() {
        execute(new ActionCommand<>(encounterActionProvider.insertDefeatSequencePoint(), "INSERT_DEFEAT_SEQUENCE_POINT"));
    }

    @Override
    public Single<String> readInput(String label) {
        return Single.create(onSub -> {
            executeHookable(encounterActionProvider.readInput(label), onSub);
        });
    }
}
