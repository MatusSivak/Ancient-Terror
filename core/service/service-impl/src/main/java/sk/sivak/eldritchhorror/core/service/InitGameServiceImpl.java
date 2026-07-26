package sk.sivak.eldritchhorror.core.service;

import java8.features.function.Function;
import sk.sivak.eldritchhorror.core.action.provider.InitGameActionProvider;
import sk.sivak.eldritchhorror.core.constants.ancientone.AncientOneInfo;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorInfo;
import sk.sivak.eldritchhorror.core.service.command.ActionCommand;

/**
 * @author msivak
 */
public class InitGameServiceImpl extends AbstractCommonService implements InitGameService {

    private InitGameActionProvider initGameActionProvider;

    public void setInitGameActionProvider(InitGameActionProvider initGameActionProvider) {
        this.initGameActionProvider = initGameActionProvider;
    }

    @Override
    public void initGame() {
        hold();
        execute(
                new ActionCommand<>(initGameActionProvider.basicInitAction(), "BASIC_INIT"),
                new ActionCommand<>(initGameActionProvider.registerBasicActions(), "REGISTER_BASIC_ACTIONS"),
                new ActionCommand<>(initGameActionProvider.numberOfPlayersAction(), "NUMBER_OF_PLAYERS"),
                new ActionCommand<>(initGameActionProvider.selectInvestigators(), "SELECT_INVESTIGATORS")
        );

        executeConvertor(InvestigatorInfo[].class, InvestigatorId[].class, toInvestigatorIdsFn());
        execute(new ActionCommand<>(initGameActionProvider.initInvestigators(), "INIT_INVESTIGATORS"));
        executeConvertor(InvestigatorId.class, Void.class, x -> null);
        execute(
                new ActionCommand<>(initGameActionProvider.selectAncientOne(), "SELECT_ANCIENT_ONE")
        );
        executeConvertor(AncientOneInfo.class, Void.class, x -> null);

        execute(new ActionCommand<>(initGameActionProvider.selectDifficulty(), "SELECT_DIFFICULTY"));

        execute(new ActionCommand<>(initGameActionProvider.toGameScreen(), "TO_GAME_SCREEN"));
        release();
    }

    @Override
    public void registerBasicActions() {
        execute(new ActionCommand<>(initGameActionProvider.registerBasicActions(), "REGISTER_BASIC_ACTIONS"));
    }

    @Override
    public void loadGame() {
        execute(new ActionCommand<>(initGameActionProvider.loadGame(), "LOAD_GAME"));
        execute(new ActionCommand<>(initGameActionProvider.loadView(), "LOAD_VIEW"));
    }

    @Override
    public void registerCurrentMysteryCard() {
        execute(new ActionCommand<>(initGameActionProvider.registerCurrentMysteryCard(), "REGISTER_CURRENT_MYSTERY_CARD"));
    }

    @Override
    public void unregisterCurrentMysteryCard() {
        execute(new ActionCommand<>(initGameActionProvider.unregisterCurrentMysteryCard(), "UNREGISTER_CURRENT_MYSTERY_CARD"));
    }

    @Override
    public void saveGame() {
        execute(new ActionCommand<>(initGameActionProvider.saveGame(), "SAVE_GAME"));
    }

    @Override
    public void initAncientOne() {
        execute(new ActionCommand<>(initGameActionProvider.initAncientOne(), "INIT_ANCIENT_ONE"));
    }

    private Function<InvestigatorInfo[], InvestigatorId[]> toInvestigatorIdsFn() {
        return in -> {
            InvestigatorId[] investigatorIds = new InvestigatorId[in.length];
            for (int i = 0; i < in.length; i++) {
                investigatorIds[i] = in[i].getInvestigatorId();
            }
            return investigatorIds;
        };
    }
}
