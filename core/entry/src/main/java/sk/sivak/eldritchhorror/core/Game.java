package sk.sivak.eldritchhorror.core;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.pay.PurchaseManager;
import rx.plugins.RxJavaHooks;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.commandqueue.CommandQueueImpl;
import sk.sivak.eldritchhorror.core.constants.AdHandler;
import sk.sivak.eldritchhorror.core.constants.tracker.AnalyticsCategory;
import sk.sivak.eldritchhorror.core.constants.tracker.AnalyticsTracker;
import sk.sivak.eldritchhorror.core.constants.tracker.GoogleServicesHolder;
import sk.sivak.eldritchhorror.core.util.PlayTimeTracker;
import sk.sivak.eldritchhorror.core.util.ServiceLocator;
import sk.sivak.eldritchhorror.core.view.ScreenType;
import sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager;
import sk.sivak.eldritchhorror.core.view.bigactors.BigActorsManager;
import sk.sivak.eldritchhorror.core.view.components.combat.FireballSmokeBuilder;
import sk.sivak.eldritchhorror.core.view.firebase.FirebaseHallOfFame;
import sk.sivak.eldritchhorror.core.view.game.InfoStage;
import sk.sivak.eldritchhorror.core.view.game.MapStage;
import sk.sivak.eldritchhorror.core.view.game.OnScreenActors;
import sk.sivak.eldritchhorror.core.view.components.skilltestgrid.GridSkillTestPrototypeScreen;

public class Game extends com.badlogic.gdx.Game {

    private PlayTimeTracker playTimeTracker;

    @Override
    public void create() {
        if (Gdx.app.getType() != Application.ApplicationType.Android) {
            Gdx.app.setLogLevel(Application.LOG_DEBUG);
        }
        start();
    }

    public void start() {
        if (Boolean.getBoolean("ancientterror.gridtest.prototype")) {
            int moves = Integer.getInteger("ancientterror.gridtest.moves", 4);
            setScreen(new GridSkillTestPrototypeScreen(moves));
            return;
        }
        ServiceLocator.setGame(this);
        checkIfTutorialWasPassed();

        ServiceLocator.getViewProvider().getInitGameView().setNewGameAction(() -> {
            ServiceLocator.getServiceProvider().getInitGameService().initGame();
        });
        ServiceLocator.getViewProvider().getInitGameView().setContinueGameAction(() -> {
            ServiceLocator.getServiceProvider().getInitGameService().loadGame();
        });

        new ScreenHolder(this).changeScreen(ScreenType.INIT_GAME);
        playTimeTracker = new PlayTimeTracker();
        RxJavaHooks.setOnError(e -> {
            ServiceLocator.getGlobalThrowableHandler().handleThrowable(e);
        });


        /*
        new FirebaseHallOfFame().fetchHallOfFameData().subscribe(list -> {
            System.out.println(list);
        });
        */
        setOverwriteUnlockedCardsFunction();
        trackGameStartedTimes();
        if (!GoogleServicesHolder.isTutorialPassed()) {
            CustomAssetManager.loadTextures1();
            CustomAssetManager.loadTextures2();
            CustomAssetManager.loadTexturesTutorial();
            CustomAssetManager.getProgressSubject().subscribe(x -> {
                        System.out.println("Progress: " + x);
                    }, err -> {
                    },
                    () -> {
                        ServiceLocator.getServiceProvider().getInitGameService().initGame();
                    });
            return;
        }

        if (Gdx.files.local("save.json").exists()) {
            CustomAssetManager.loadTextures1();
            CustomAssetManager.loadTextures2();
        } else {
            ServiceLocator.getServiceProvider().getInitGameService().initGame();
        }

    }

    private void setOverwriteUnlockedCardsFunction() {
        GoogleServicesHolder.setOverwriteUnlockedAssetsFunction(assetIds -> {
            ServiceLocator.getUnlockedAssetsRepository().saveData(assetIds);
        });
        GoogleServicesHolder.setOverwriteUnlockedArtifactsFunction(artifactsIds -> {
            ServiceLocator.getUnlockedArtifactsRepository().saveData(artifactsIds);
        });
    }

    private void trackGameStartedTimes() {
        Preferences preferences = Gdx.app.getPreferences("AncientTerror.xml");
        int gameStartedCount = preferences.getInteger("GAME_STARTED_TIMES", 0);
        GoogleServicesHolder.getAnalyticsTracker().trackInteraction(AnalyticsCategory.GAME_STARTED_TIMES, String.valueOf(gameStartedCount+1));
        preferences.putInteger("GAME_STARTED_TIMES", gameStartedCount+1);
        preferences.flush();
    }

    private void checkIfTutorialWasPassed() {
        Preferences preferences = Gdx.app.getPreferences("AncientTerror.xml");
        boolean tutorialPassed = preferences.getBoolean("TUTORIAL_PASSED", false);
        GoogleServicesHolder.setTutorialPassed(tutorialPassed);
        GoogleServicesHolder.setTutorialPassedAction(() -> {
            preferences.putBoolean("TUTORIAL_PASSED", true);
            preferences.flush();
        });
    }

    @Override
    public void render() {
        try {
            super.render();
            CommandQueueImpl.get().tick();
            if (playTimeTracker != null) {
                playTimeTracker.accept(Gdx.graphics.getDeltaTime());
            }
        } catch (Exception e) {
            ServiceLocator.getGlobalThrowableHandler().handleThrowable(e);
        }
    }

    public void setAnalyticsTracker(AnalyticsTracker analyticsTracker) {
        GoogleServicesHolder.setAnalyticsTracker(analyticsTracker);
    }

    @Override
    public void pause() {
        super.pause();
        GoogleServicesHolder.getAnalyticsTracker().trackInteraction(AnalyticsCategory.GAME_STATE, "pause");
    }

    @Override
    public void resume() {
        super.resume();
        GoogleServicesHolder.getAnalyticsTracker().trackInteraction(AnalyticsCategory.GAME_STATE, "resume");
    }

    @Override
    public void dispose() {
        GoogleServicesHolder.getAnalyticsTracker().trackInteraction(AnalyticsCategory.GAME_STATE, "dispose");
        MapStage.nullifyInstance();
        InfoStage.nullifyInstance();
        ServicePlatform.nullifyInstance();
        sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform.nullifyInstance();
        OnScreenActors.nullifyInstance();
        BigActorsManager.nullifyInstance();
        FireballSmokeBuilder.nullifyInstance();
        CommandQueueImpl.nullifyInstance();
        CustomAssetManager.nullifyInstance();
        ServiceLocator.nullifyInstance();
    }

    public void setAdHandler(AdHandler adHandler) {
        GoogleServicesHolder.setAdHandler(adHandler);
    }

    public void setPurchaseManager(PurchaseManager purchaseManager) {
        GoogleServicesHolder.setPurchaseManager(purchaseManager);
    }
}
