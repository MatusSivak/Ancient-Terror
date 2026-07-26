package sk.sivak.eldritchhorror.core.service;

import rx.Single;
import sk.sivak.eldritchhorror.core.action.provider.InvestigatorActionProvider;
import sk.sivak.eldritchhorror.core.constants.gate.GateInfo;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.constants.question.Question;
import sk.sivak.eldritchhorror.core.eventtype.data.investigator.DelayedData;
import sk.sivak.eldritchhorror.core.eventtype.data.investigator.InvestigatorRestriction;
import sk.sivak.eldritchhorror.core.eventtype.data.investigator.LoseImprovementData;
import sk.sivak.eldritchhorror.core.service.command.ActionCommand;

/**
 * @author msivak
 */
public class InvestigatorServiceImpl extends AbstractCommonService implements InvestigatorService {

    private InvestigatorActionProvider investigatorActionProvider;
    private GameService gameService;


    public void setInvestigatorActionProvider(InvestigatorActionProvider investigatorActionProvider) {
        this.investigatorActionProvider = investigatorActionProvider;
    }

    public void setGameService(GameService gameService) {
        this.gameService = gameService;
    }

    @Override
    public void changeActiveInvestigator() {
        execute(new ActionCommand<>(investigatorActionProvider.changeActiveInvestigator(), "CHANGE_ACTIVE_INVESTIGATOR"));
    }

    @Override
    public void unsetActiveInvestigator() {
        execute(new ActionCommand<>(investigatorActionProvider.unsetActiveInvestigator(), "UNSET_ACTIVE_INVESTIGATOR"));
    }

    @Override
    public void changeActiveInvestigator(InvestigatorId investigatorId) {
        execute(new ActionCommand<>(investigatorActionProvider.changeActiveInvestigator(investigatorId), "CHANGE_ACTIVE_INVESTIGATOR"));
    }

    @Override
    public void showActiveInvestigator(boolean displayTransition) {
        hold();
        execute(new ActionCommand<>(investigatorActionProvider.findActiveInvestigator(), "FIND_ACTIVE_INVESTIGATOR"));
        executeHookable(investigatorActionProvider.showActiveInvestigator(displayTransition));
        release();
    }

    @Override
    public void changeAndShowActiveInvestigator() {
        hold();
        changeActiveInvestigator();
        showActiveInvestigator(true);
        release();
    }

    @Override
    public Single<InvestigatorId> selectInvestigator(InvestigatorRestriction investigatorRestriction) {
        return Single.create(onSub -> executeHookable(investigatorRestriction, investigatorActionProvider.selectInvestigator(), onSub));
    }

    @Override
    public void improveSkill(Stat skill) {
        executeHookable(skill, investigatorActionProvider.improveSkill());
    }

    @Override
    public void loseImprovement(LoseImprovementData loseImprovement) {
        executeHookable(loseImprovement, investigatorActionProvider.loseImprovement());
    }

    @Override
    public void becomeDelayed(DelayedData delayedData) {
        executeHookable(delayedData, investigatorActionProvider.becomeDelayed());
    }

    @Override
    public void endDelayed(InvestigatorId investigatorId) {
        executeHookable(investigatorId, investigatorActionProvider.endDelayed());
    }

    @Override
    public void lostInTimeAndSpace(InvestigatorId investigatorId) {
        executeHookable(investigatorId, investigatorActionProvider.lostInTimeAndSpace());
    }

    @Override
    public void showLostInTimeAndSpace(InvestigatorId investigatorId) {
        executeHookable(investigatorId, investigatorActionProvider.showLostInTimeAndSpace());
    }


    @Override
    public void hideLostInTimeAndSpace(InvestigatorId investigatorId) {
        executeHookable(investigatorId, investigatorActionProvider.hideLostInTimeAndSpace());
    }


    @Override
    public void removeInvestigatorFromPlace(InvestigatorId investigatorId) {
        execute(new ActionCommand<>(investigatorActionProvider.removeInvestigatorFromPlace(investigatorId), "REMOVE_INVESTIGATOR_FROM_PLACE"));
    }

    @Override
    public void spawnInvestigator(InvestigatorId investigatorId, LocationId locationId) {
        executeHookable(investigatorActionProvider.spawnInvestigator(investigatorId, locationId));
    }

    @Override
    public void putGateToTop(GateInfo selectedGate) {
        execute(new ActionCommand<>(investigatorActionProvider.putGateToTop(selectedGate), "PUT_GATE_TO_TOP"));
    }

    @Override
    public void putGateToBottom(GateInfo selectedGate) {
        execute(new ActionCommand<>(investigatorActionProvider.putGateToBottom(selectedGate), "PUT_GATE_TO_BOTTOM"));
    }

    @Override
    public Single<Boolean> spendCluesAsGroup(int cluesCount) {
        return Single.create(onSub -> executeHookable(cluesCount, investigatorActionProvider.spendCluesAsGroup(), onSub));
    }

    @Override
    public void devourInvestigator(InvestigatorId investigatorId) {
        executeHookable(actionProvider.devourInvestigator(investigatorId));
    }

    @Override
    public void defeatInvestigator(InvestigatorId investigatorId, boolean health) {
        executeHookable(actionProvider.defeatInvestigator(investigatorId, health));
    }

    @Override
    public void discardDefeatedInvestigator(InvestigatorId investigatorId) {
        execute(new ActionCommand<>(actionProvider.discardDefeatedInvestigator(investigatorId), "DISCARD_DEFEATED_INVESTIGATOR"));
    }

    @Override
    public void removeDefeatedInvestigator(InvestigatorId investigatorId) {
        execute(new ActionCommand<>(actionProvider.removeDefeatedInvestigator(investigatorId), "REMOVE_DEFEATED_INVESTIGATOR"));
    }



    @Override
    public void selectReplacingInvestigator(InvestigatorId removedInvestigator) {
        Question<Object> question = new Question<>();
        question.setTitle("Select replacement for the " + removedInvestigator.toString());
        question.setPortraitBeforeTitle(removedInvestigator);
        question.setOptions(Question.Option.okOption);

        gameService.ask(question).subscribe(ok -> {
            execute(new ActionCommand<>(investigatorActionProvider.selectReplacingInvestigator(removedInvestigator), "SELECT_REPLACING_INVESTIGATOR"));
        });
    }

    @Override
    public void selectNewLeadInvestigator(boolean reorder) {
        execute(new ActionCommand<>(actionProvider.selectNewLeadInvestigator(reorder), "SELECT_NEW_LEAD_INVESTIGATOR"));
    }

    @Override
    public void replaceInvestigators() {
        executeHookable(actionProvider.replaceInvestigators());
    }
}
