package sk.sivak.eldritchhorror.core;

import com.badlogic.gdx.math.MathUtils;
import sk.sivak.eldritchhorror.core.constants.artifact.ArtifactId;
import sk.sivak.eldritchhorror.core.constants.asset.AssetId;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.spell.SpellId;
import sk.sivak.eldritchhorror.core.service.GameServiceImpl;
import sk.sivak.eldritchhorror.core.util.ServiceLocator;
import sk.sivak.eldritchhorror.core.view.ScreenType;
import sk.sivak.eldritchhorror.core.view.components.combat.CombatScreen;
import sk.sivak.eldritchhorror.core.view.components.halloffame.HallOfFameScreen;
import sk.sivak.eldritchhorror.core.view.game.GameViewImpl;
import sk.sivak.eldritchhorror.core.view.handler.ChangeScreenHandler;
import sk.sivak.eldritchhorror.core.view.handler.StartGameHandler;
import sk.sivak.eldritchhorror.core.view.initgame.InitGameViewImpl;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * @author msivak
 */
public class ScreenHolder implements ChangeScreenHandler, StartGameHandler {

    private Game game;

    ScreenHolder(Game game) {
        this.game = game;
    }

    private InitGameViewImpl toInitGameView() {
        InitGameViewImpl initGameView = ServiceLocator.getViewProvider().getInitGameView();
        initGameView.setDefaultSkin(ServiceLocator.getDefaultSkin());
        initGameView.setChangeScreenHandler(this);
        initGameView.setReplayTutorialAction(() -> ServiceLocator.getServiceProvider().getInitGameService().initGame());
        game.setScreen(initGameView);
        return initGameView;
    }

    private void toCollectionView() {
        CombatScreen combatScreen = ServiceLocator.getViewProvider().getCombatScreen();
        combatScreen.setChangeScreenHandler(this);
        combatScreen.setDefaultSkin(ServiceLocator.getDefaultSkin());
        combatScreen.setUnlockedAssets(ServiceLocator.getUnlockedAssetsRepository().getUnlockedAssets());
        combatScreen.setUnlockedArtifacts(ServiceLocator.getUnlockedArtifactsRepository().getUnlockedArtifacts());


        combatScreen.setDiscoveredAssets(ServiceLocator.getDiscoveredAssetsRepository().getDiscovered());
        combatScreen.setDiscoveredArtifacts(ServiceLocator.getDiscoveredArtifactsRepository().getDiscovered());
        combatScreen.setDiscoveredSpells(ServiceLocator.getDiscoveredSpellsRepository().getDiscovered());
        combatScreen.setDiscoveredConditions(ServiceLocator.getDiscoveredConditionsRepository().getDiscovered());
        game.setScreen(combatScreen);
    }

    private void toHallOfFameView() {
        HallOfFameScreen hallOfFameScreen = ServiceLocator.getViewProvider().getHallOfFameScreen();
        hallOfFameScreen.setChangeScreenHandler(this);
        hallOfFameScreen.setDefaultSkin(ServiceLocator.getDefaultSkin());
        game.setScreen(hallOfFameScreen);
    }


    private GameViewImpl toGameView() {
        GameViewImpl gameView = ServiceLocator.getViewProvider().getGameView();
        gameView.setDefaultSkin(ServiceLocator.getDefaultSkin());
        gameView.setStartGameHandler(this);
        gameView.setChangeScreenHandler(this);
        game.setScreen(gameView);
        return gameView;
    }

    @Override
    public void changeScreen(ScreenType screenType) {
        switch (screenType) {
            case INIT_GAME:
                toInitGameView();
                break;
            case GAME:
                toGameView();
                break;
            case CARDS:
                toCollectionView();
                break;
            case HALL_OF_FAME:
                toHallOfFameView();
                break;
        }
    }

    @Override
    public void resetInitGameView() {
        ServiceLocator.getServiceProvider().getInitGameService().initGame();
        ServiceLocator.getViewProvider().getInitGameView().reset();
    }

    @Override
    public void startGame() {
        GameServiceImpl gameService = ServiceLocator.getServiceProvider().getGameService();
        int players = ServiceLocator.getModelProvider().getInvestigators().getPlayers();
//		ServiceLocator.getServiceProvider().getDoomOmenService().retreatDoom();
        gameService.startGame(players);
    }
}
