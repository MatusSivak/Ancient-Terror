package sk.sivak.eldritchhorror.core.action.provider;

import sk.sivak.eldritchhorror.core.action.Action;
import sk.sivak.eldritchhorror.core.action.DirectEventAction;
import sk.sivak.eldritchhorror.core.constants.ancientone.AncientOneInfo;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorInfo;

/**
 * @author msivak
 */
public interface InitGameActionProvider {

    Action<Object, Void> basicInitAction();

    Action<Void, Void> registerBasicActions();

    Action<Void, Integer> numberOfPlayersAction();

    Action<Integer, InvestigatorInfo[]> selectInvestigators();

    DirectEventAction<InvestigatorId[]> initInvestigators();

    Action<Void, AncientOneInfo> selectAncientOne();

    Action<Object, Void> initAncientOne();

    Action<Void, Void> toGameScreen();

    Action<Void, Void> selectDifficulty();

    Action<Object, Void> registerCurrentMysteryCard();

    Action<Object, Void> unregisterCurrentMysteryCard();

    Action<Object, Void> saveGame();

    Action<Object, Object> loadGame();

    Action<Object, Void> loadView();
}
