package sk.sivak.eldritchhorror.core.view.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.RepeatAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import java8.features.function.Consumer;
import java8.features.stream.Stream;
import rx.Completable;
import sk.sivak.eldritchhorror.core.constants.ViewProperties;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorBasics;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.constants.location.LocationType;
import sk.sivak.eldritchhorror.core.controller.GameController;
import sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager;
import sk.sivak.eldritchhorror.core.view.components.combat.ThunderEffect;
import sk.sivak.eldritchhorror.core.view.game.MapStage;
import sk.sivak.eldritchhorror.core.view.map.LocationPositionResolver;
import sk.sivak.eldritchhorror.core.view.map.MapUtils;
import sk.sivak.eldritchhorror.core.view.map.helper.MoveCameraToLocationHelper;
import sk.sivak.eldritchhorror.core.view.map.investigator.InvestigatorImage;
import sk.sivak.eldritchhorror.core.view.music.NewMusicBox;

import java.util.List;

import static sk.sivak.eldritchhorror.core.constants.ViewProperties.FAST_ACTION_DURATION;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.LOST_IN_TIME_AND_SPACE;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.SPACE;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.getTextureRegionDrawable;
import static sk.sivak.eldritchhorror.core.view.map.helper.MoveCameraToLocationHelper.moveCameraToPosition;
import static sk.sivak.eldritchhorror.core.view.utils.RectangleUtils.randomPointInRectangle;

public class BackgroundUtils {
    private Stage backgroundStage;
    private final GameController gameController;
    private String currentBackgroundName;

    public BackgroundUtils(Stage backgroundStage, GameController gameController) {
        this.backgroundStage = backgroundStage;
        this.gameController = gameController;
    }

    public void toLostInTimeAndSpaceBackground() {
        NewMusicBox.getInstance().changeMusic(NewMusicBox.PREPARE_BOARD_OR_TYPEWRITER);
        Image newBackground = toBackground(LOST_IN_TIME_AND_SPACE);
        newBackground.setScale(1.25f);
        float width = ViewProperties.VIEWPORT_WIDTH * newBackground.getScaleX() - ViewProperties.VIEWPORT_WIDTH;
        float height = ViewProperties.VIEWPORT_HEIGHT * newBackground.getScaleY() - ViewProperties.VIEWPORT_HEIGHT;
        MyMoveToAction moveToAction = new MyMoveToAction(new Vector2(0, 0),
                () -> randomPointInRectangle(new Vector2(width / 2f, height / 2f), width, height), 30f, 1.25f);
        newBackground.addAction(Actions.repeat(RepeatAction.FOREVER,
                moveToAction));
    }

    public Completable showLocationBackground(LocationId locationId, List<InvestigatorBasics> investigatorsAtLocation) {
        Consumer<Image> imageConsumer = image -> {
            NewMusicBox.getInstance().changeMusic(NewMusicBox.PREPARE_BOARD_OR_TYPEWRITER);
            image.setOrigin(ViewProperties.VIEWPORT_WIDTH / 2f, ViewProperties.VIEWPORT_HEIGHT / 2f);
            scaleDownBackground(image);
        };
        return showBackground(
                "encounter/background/" + locationId.lastWordToCamelCase() + ".jpg",
                imageConsumer,
                investigatorsAtLocation);
    }

    private void scaleDownBackground(Image image) {
        if (!Gdx.app.getPreferences("AncientTerror.xml").getBoolean("strobe_disabled")) {
            image.setScale(1.5f);
            image.addAction(Actions.scaleTo(1f, 1f, 5f, Interpolation.sineOut));
        }
    }

    public Completable showLocationTypeBackground(LocationType locationType, List<InvestigatorBasics> investigatorsAtLocation) {
        return showBackground("encounter/background/" + locationType.getValue() + ".jpg", image -> {
            NewMusicBox.getInstance().changeMusic(NewMusicBox.PREPARE_BOARD_OR_TYPEWRITER);
            scaleDownBackground(image);
        }, investigatorsAtLocation);
    }

    public Completable showResearchBackground(LocationType locationType, List<InvestigatorBasics> investigatorsAtLocation) {
        return showBackground("encounter/background/Research_" + locationType.getValue() + ".jpg", image -> {
            NewMusicBox.getInstance().changeMusic(NewMusicBox.PREPARE_BOARD_OR_TYPEWRITER);
            scaleDownBackground(image);
        }, investigatorsAtLocation);
    }

    public Completable showCustomBackground(String backgroundId, List<InvestigatorBasics> investigatorsAtLocation) {
        return showBackground("encounter/background/" + backgroundId + ".jpg", image -> {
            NewMusicBox.getInstance().changeMusic(NewMusicBox.PREPARE_BOARD_OR_TYPEWRITER);
            scaleDownBackground(image);
        }, investigatorsAtLocation);
    }

    public Completable showWorldBackground() {
        return Completable.create(onSub -> {
            MapStage.setWorldVisibility(true);
            for (Actor investigatorImage : MapStage.getAllActors(MapStage.getInvestigatorLayer())) {
                investigatorImage.addAction(Actions.alpha(1f, FAST_ACTION_DURATION));
            }
            NewMusicBox.getInstance().changeMusic(NewMusicBox.TRACK_HORROR_GAME_INTRO);
            MapStage.addDragAndZoomListeners();
            MapUtils.enableCameraPositionFix();
            toBackground(SPACE);
            backgroundStage.addAction(Actions.delay(1f, Actions.run(onSub::onCompleted)));
        });
    }

    public Completable showCombatBackground(List<InvestigatorBasics> investigatorsAtLocation) {
        return showBackground("encounter/background/combat.jpg", newBackground -> {
            NewMusicBox.getInstance().changeMusic(NewMusicBox.TRACK_COMBAT);
            newBackground.setScale(1.25f);
            new ThunderEffect(newBackground).execute();
            newBackground.setOrigin(ViewProperties.VIEWPORT_WIDTH/2f, ViewProperties.VIEWPORT_HEIGHT/2f);
            float width = Math.abs(ViewProperties.VIEWPORT_WIDTH * newBackground.getScaleX() - ViewProperties.VIEWPORT_WIDTH);
            float height = Math.abs(ViewProperties.VIEWPORT_HEIGHT * newBackground.getScaleY() - ViewProperties.VIEWPORT_HEIGHT);
            MyMoveToAction moveToAction = new MyMoveToAction(new Vector2(0, 0),
                    () -> randomPointInRectangle(new Vector2(0, 0),
                            width, height), 100f, 1.25f);
            newBackground.addAction(Actions.repeat(RepeatAction.FOREVER,
                    moveToAction));
        }, investigatorsAtLocation);
    }

    private Completable showBackground(String textureName,
                                       Consumer<Image> withBackgroundImage,
                                       List<InvestigatorBasics> investigatorsAtLocation) {
        return Completable.create(onSub -> {
            hideInvestigatorsNotOnThisLocation(investigatorsAtLocation);

            backgroundStage.addAction(new FastForwardAction<>(Actions.sequence(
                    Actions.run(() -> {
                        if (!investigatorsAtLocation.isEmpty()) {
                            Vector2 centerPosition = LocationPositionResolver.resolve(investigatorsAtLocation.get(0).getLocationId());
                            Vector2 newCameraPosition = new Vector2(
                                    centerPosition.x + 310,
                                    centerPosition.y + 150);
                            moveCameraToPosition(newCameraPosition, onSub, 1.0f);
                        }
                        MapUtils.disableCameraPositionFix();
                        MapStage.setWorldVisibility(false);
                        MapStage.removeDragAndZoomListeners();
                        CustomAssetManager.getTextureAsync(textureName).subscribe(texture -> {
                            withBackgroundImage.accept(toBackground(textureName));
                        });
                        MapStage.getInvestigatorLayer().clearActions();
                    })
            )));
        });
    }

    private void hideInvestigatorsNotOnThisLocation(List<InvestigatorBasics> investigatorsAtLocation) {
        for (Actor actor : MapStage.getAllActors(MapStage.getInvestigatorLayer())) {
            InvestigatorImage investigatorImage = (InvestigatorImage) actor;
            boolean isOnCurrentSpace = Stream.anyMatch(investigatorsAtLocation,
                    ib -> ib.getInvestigatorId() == investigatorImage.getInvestigatorId());
            if (isOnCurrentSpace) {
                continue;
            }
            investigatorImage.addAction(Actions.alpha(0, FAST_ACTION_DURATION));
        }
    }

    public Completable hideBackground() {
        return Completable.create(onSub -> {
            String backgroundName = gameController.getPreviousBackgroundName() == null ? SPACE : gameController.getPreviousBackgroundName();
            currentBackgroundName = currentBackgroundName == null ? SPACE : currentBackgroundName;
            if (currentBackgroundName.equals(backgroundName)) {
                if (backgroundName.equals(SPACE)) {
                    onSub.onCompleted(); // SPACE -> SPACE
                    return;
                } else {
                    backgroundName = SPACE; // investigator1: LONDON -> investigator1: LONDON
                }
            }
            NewMusicBox.getInstance().backToPreviousTrack();
            MoveCameraToLocationHelper.setEnabled(true);
            backgroundStage.addAction(new FastForwardAction<>(Actions.sequence(
                    Actions.run(new ChangeBackgroundAction(backgroundName)),
                    new FastForwardAction<>(Actions.delay(1f, Actions.run(onSub::onCompleted)))
            )));
        });
    }

    private class ChangeBackgroundAction implements Runnable {

        private String backgroundName;

        public ChangeBackgroundAction(String backgroundName) {
            this.backgroundName = backgroundName;
        }

        @Override
        public void run() {
            if (SPACE.equals(backgroundName)) {
                MapStage.setWorldVisibility(true);
                for (Actor investigatorImage : MapStage.getAllActors(MapStage.getInvestigatorLayer())) {
                    investigatorImage.addAction(Actions.alpha(1f, FAST_ACTION_DURATION));
                }
                MapStage.addDragAndZoomListeners();
                MapUtils.enableCameraPositionFix();
            }

            CustomAssetManager.getTextureAsync(backgroundName).subscribe(ok -> {
                toBackground(backgroundName);
            });

        }
    }


    public Image toBackground(String backgroundName) {
        Actor background = backgroundStage.getActors().get(0);
        currentBackgroundName = backgroundName;
        float duration = 1f;
        background.addAction(Actions.sequence(
                Actions.alpha(0f, duration),
                Actions.removeActor()
        ));
        MoveCameraToLocationHelper.setEnabled(backgroundName.equals(SPACE));
        Image newBackground = new Image(getTextureRegionDrawable(backgroundName));
        newBackground.setScaling(Scaling.fit);
        newBackground.setOrigin(Align.center);
        newBackground.setSize(ViewProperties.VIEWPORT_WIDTH, ViewProperties.VIEWPORT_HEIGHT);
        newBackground.getColor().a = 0f;
        newBackground.addAction(Actions.alpha(1f, duration));
        backgroundStage.addActor(newBackground);
        return newBackground;
    }
}
