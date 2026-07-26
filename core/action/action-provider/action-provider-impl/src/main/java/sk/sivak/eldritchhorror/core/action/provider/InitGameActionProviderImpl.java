package sk.sivak.eldritchhorror.core.action.provider;

import sk.sivak.eldritchhorror.core.action.Action;
import sk.sivak.eldritchhorror.core.action.DirectEventAction;
import sk.sivak.eldritchhorror.core.action.impl.initgame.*;
import sk.sivak.eldritchhorror.core.action.impl.saveload.LoadGameAction;
import sk.sivak.eldritchhorror.core.action.impl.saveload.LoadViewAction;
import sk.sivak.eldritchhorror.core.action.impl.saveload.SaveGameAction;
import sk.sivak.eldritchhorror.core.constants.ancientone.AncientOneInfo;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorInfo;
import sk.sivak.eldritchhorror.core.model.save.SaveDataRead;

/**
 * @author msivak
 */
public class InitGameActionProviderImpl extends AbstractActionProvider implements InitGameActionProvider {

    @Override
    public Action<Object, Void> basicInitAction() {
        return new BasicInitAction();
    }

    @Override
    public Action<Void, Void> registerBasicActions() {
        return new RegisterBasicActionsAction();
    }

    @Override
    public Action<Void, Integer> numberOfPlayersAction() {
        return new NumberOfPlayersAction();
    }

    @Override
    public Action<Integer, InvestigatorInfo[]> selectInvestigators() {
        return new SelectInvestigatorsAction();
    }

    @Override
    public DirectEventAction<InvestigatorId[]> initInvestigators() {
        return new InitInvestigatorsAction();
    }

    @Override
    public Action<Void, AncientOneInfo> selectAncientOne() {
        return new SelectAncientOneAction();
    }

    @Override
    public Action<Object, Void> initAncientOne() {
        return new InitAncientOneAction();
    }

    @Override
    public Action<Void, Void> toGameScreen() {
        return new ToGameScreenAction();
    }

    @Override
    public Action<Void, Void> selectDifficulty() {
        return new SelectDifficultyAction();
    }

    @Override
    public Action<Object, Void> registerCurrentMysteryCard() {
        return new RegisterCurrentMysteryCardAction();
    }

    @Override
    public Action<Object, Void> unregisterCurrentMysteryCard() {
        return new UnregisterCurrentMysteryCardAction();
    }

    @Override
    public Action<Object, Void> saveGame() {
        return new SaveGameAction();
    }

    @Override
    public Action<Object, Object> loadGame() {
        return new LoadGameAction();
    }

    @Override
    public Action<Object, Void> loadView() {
        return new LoadViewAction();
    }
}
