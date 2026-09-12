package sk.sivak.eldritchhorror.core.view.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import sk.sivak.eldritchhorror.core.constants.AdHandler;
import sk.sivak.eldritchhorror.core.constants.tracker.GoogleServicesHolder;
import sk.sivak.eldritchhorror.core.view.ScreenType;
import sk.sivak.eldritchhorror.core.view.components.RestartConfirmationDialog;
import sk.sivak.eldritchhorror.core.view.bigactors.BigActorsManager;
import sk.sivak.eldritchhorror.core.view.handler.ChangeScreenHandler;
import sk.sivak.eldritchhorror.core.view.map.CameraActor;
import sk.sivak.eldritchhorror.core.view.map.MapUtils;
import sk.sivak.eldritchhorror.core.view.map.helper.MoveCameraToLocationHelper;
import sk.sivak.eldritchhorror.core.view.music.NewMusicBox;

import static sk.sivak.eldritchhorror.core.constants.tracker.AnalyticsCategory.AD_MOB;

public class RestartGameAction {

    private ChangeScreenHandler changeScreenHandler;
    private Runnable clearQueueAction;

    public RestartGameAction(ChangeScreenHandler changeScreenHandler, Skin skin) {
        this.changeScreenHandler = changeScreenHandler;
    }

    public void restartGame() {
        if (Gdx.app.getPreferences("AncientTerror.xml").getBoolean("no_ads", false)) {
            InfoStage.getMenuButton().remove();
            justRestartGame();
            return;
        }

        RestartConfirmationDialog.show(InfoStage.getStageSafe()).subscribe(answer -> {
            if (!answer) {
                return;
            }
            GoogleServicesHolder.getAdHandler().showRewardedAd(new AdHandler.AdCallbacks()
                    .setOnAdRewardedAction(() -> {
                        Gdx.app.postRunnable(() -> {
                            GoogleServicesHolder.getAnalyticsTracker().trackInteraction(AD_MOB, "rewarded_ad", "reward_granted");
                            InfoStage.getMenuButton().remove();
                            justRestartGame();
                        });
                    })
                    .setOnAdFailedToLoadAction(errorCode ->
                            GoogleServicesHolder.getAnalyticsTracker().trackInteraction(AD_MOB, "rewarded_ad", "show_failed_" + errorCode)));
        });


    }

    public void justRestartGame() {
        cleanMapStage();
        BigActorsManager.reset();
        InfoStage.reset();
        clearQueueAction.run();
        Gdx.files.local("save.json").delete();
        changeScreenHandler.changeScreen(ScreenType.INIT_GAME);
        changeScreenHandler.resetInitGameView();
        NewMusicBox.getInstance().reset();
    }

    private void cleanMapStage() {
        for (Actor actor : MapStage.getStage().getActors()) {
            if (actor instanceof CameraActor) {
                actor.remove();
            }
        }
        MapStage.getTouchBlockerLayer().clear();
        MapStage.getLocationHighlightLayer().clear();
        MoveCameraToLocationHelper.setEnabled(true);
        MapStage.getStage().getRoot().clearActions();
        MapUtils.enableCameraPositionFix();
        MapStage.setWorldVisibility(true);
        MapStage.addDragAndZoomListeners();
        MapStage.brightenWorld();
        MapStage.getDefeatedInvestigatorLayer().clear();
        MapStage.clearIdActorMap();
    }

    public void setClearQueueAction(Runnable clearQueueAction) {
        this.clearQueueAction = clearQueueAction;
    }


}
