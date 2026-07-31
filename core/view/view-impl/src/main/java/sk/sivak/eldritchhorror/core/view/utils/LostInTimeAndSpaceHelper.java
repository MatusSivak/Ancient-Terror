package sk.sivak.eldritchhorror.core.view.utils;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.FloatAction;
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction;
import com.badlogic.gdx.scenes.scene2d.actions.RepeatAction;
import java8.features.function.Supplier;
import java8.features.stream.Stream;
import rx.Completable;
import sk.sivak.eldritchhorror.core.constants.ViewProperties;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorBasics;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorInfo;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.controller.GameController;
import sk.sivak.eldritchhorror.core.view.game.GameViewImpl;
import sk.sivak.eldritchhorror.core.view.game.InfoStage;
import sk.sivak.eldritchhorror.core.view.game.MapStage;
import sk.sivak.eldritchhorror.core.view.map.MapUtils;
import sk.sivak.eldritchhorror.core.view.map.helper.DistanceHelper;
import sk.sivak.eldritchhorror.core.view.map.helper.MoveCameraToLocationHelper;
import sk.sivak.eldritchhorror.core.view.map.investigator.InvestigatorImage;
import sk.sivak.eldritchhorror.core.view.map.investigator.InvestigatorUtils;

import java.util.List;

import static java8.features.stream.Stream.collectToList;
import static sk.sivak.eldritchhorror.core.constants.ViewProperties.CAMERA_ZOOM_SPEED;
import static sk.sivak.eldritchhorror.core.constants.ViewProperties.FAST_ACTION_DURATION;
import static sk.sivak.eldritchhorror.core.constants.ViewProperties.MAP_HEIGHT;
import static sk.sivak.eldritchhorror.core.constants.ViewProperties.MAP_WIDTH;
import static sk.sivak.eldritchhorror.core.constants.ViewProperties.VIEWPORT_HEIGHT;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.SPACE;
import static sk.sivak.eldritchhorror.core.view.map.investigator.InvestigatorUtils.findInvestigatorsOffset;
import static sk.sivak.eldritchhorror.core.view.utils.UiText.get;

public class LostInTimeAndSpaceHelper {

    private final GameController controller;
    private final Stage backgroundStage;
    private BackgroundUtils backgroundUtils;

    public LostInTimeAndSpaceHelper(GameController controller, Stage backgroundStage) {
        this.controller = controller;
        this.backgroundStage = backgroundStage;
    }

    public void setBackgroundUtils(BackgroundUtils backgroundUtils) {
        this.backgroundUtils = backgroundUtils;
    }

    public Completable showInvestigatorLostInTimeAndSpace(InvestigatorId investigatorId) {
        return Completable.create(onSub -> {
            InfoStage.displayTextDontHide(get("lostInTimeAndSpace.title"));
            InfoStage.getInvestigatorHud().hide().subscribe();
            MoveCameraToLocationHelper.moveCameraToPosition(new Vector2(MAP_WIDTH/2f, MAP_HEIGHT/2f), onSub, 0.75f);
            backgroundUtils.toLostInTimeAndSpaceBackground();
            MapStage.setWorldVisibility(false);
            MapStage.removeDragAndZoomListeners();
            InvestigatorImage investigatorImage = createInvestigatorImage(investigatorId);
            investigatorImage.setAsteroidVisible(true);


            InvestigatorImage otherInvestigatorImage = new InvestigatorImage(investigatorId, controller);
            otherInvestigatorImage.setVisible(false);
            MapStage.addToLayer(otherInvestigatorImage, InvestigatorUtils.getIdLayerResolver(investigatorId, true));
            MapStage.addToLayer(investigatorImage, InvestigatorUtils.getIdLayerResolver(investigatorId, true));
            MapStage.addToLayer(otherInvestigatorImage, InvestigatorUtils.getIdLayerResolver(investigatorId, true));
        });
    }

    private InvestigatorImage createInvestigatorImage(InvestigatorId investigatorId) {
        InvestigatorImage investigatorImage = new InvestigatorImage(investigatorId, controller);
        investigatorImage.getColor().a = 0f;
        investigatorImage.addAction(new FastForwardAction<>(Actions.alpha(1f, 1f)));
        investigatorImage.setPosition(
                MAP_WIDTH / 2f - investigatorImage.getWidth() / 2f,
                MAP_HEIGHT / 2f - investigatorImage.getHeight() / 2f
        );
        Supplier<Vector2> randomPointSupplier = () -> {
            float centerX = MAP_WIDTH / 1.95f - investigatorImage.getWidth() / 2f;
            float centerY = MAP_HEIGHT / 1.95f - investigatorImage.getHeight() / 2.5f;
            float width = ViewProperties.VIEWPORT_WIDTH * 0.5f;
            float height = ViewProperties.VIEWPORT_HEIGHT * 0.44f;
            return RectangleUtils.randomPointInRectangle(new Vector2(centerX, centerY), width, height);
        };
        MyMoveToAction moveToAction = new MyMoveToAction(
                new Vector2(investigatorImage.getX(), investigatorImage.getY()), randomPointSupplier, 50f, 1f);
        investigatorImage.addAction(Actions.repeat(RepeatAction.FOREVER,
                moveToAction));
        return investigatorImage;
    }

    public Completable removeInvestigator(InvestigatorId investigatorId, LocationId locationId) {
        return Completable.create(onSub -> {
            InvestigatorUtils.InvestigatorIdLayerResolver idLayerResolver =
                    InvestigatorUtils.getIdLayerResolver(investigatorId, false);
            List<InvestigatorImage> investigatorImages = MapStage.getActor(idLayerResolver);
            if (investigatorImages.isEmpty()) { // when game is loaded
                onSub.onCompleted();
                return;
            }
            for (int i = 0; i < investigatorImages.size(); i++) {
                if (i==1) {
                    investigatorImages.get(i).addAction(Actions.sequence(
                            Actions.delay(0.5f), // to wait for card to hide
                            Actions.alpha(0, FAST_ACTION_DURATION),
                            Actions.run(onSub::onCompleted),
                            Actions.run(() -> {
                                List<InvestigatorId> otherInvestigators = collectToList(
                                        Stream.map(
                                                collectToList(controller.getInvestigatorsAtLocation(locationId),
                                                        inv -> inv.getInvestigatorId() != investigatorId),
                                                InvestigatorInfo::getInvestigatorId));
                                otherInvestigators.addAll(controller.getDefeatedInvestigatorsAtLocation(locationId));
                                findInvestigatorsOffset(otherInvestigators, 0);

                                MapStage.removeActor(idLayerResolver);
                            })
                    ));
                } else {
                    investigatorImages.get(i).addAction(Actions.sequence(
                            Actions.delay(0.5f), // to wait for card to hide
                            Actions.alpha(0, FAST_ACTION_DURATION))
                    );
                }
            }
        });
    }

    public Completable hideInvestigatorLostInTimeAndSpace(InvestigatorId investigatorId) {
        return Completable.create(onSub -> {
            InfoStage.hideLabel(get("lostInTimeAndSpace.title"));
            backgroundUtils.hideBackground().subscribe();

            InvestigatorUtils.InvestigatorIdLayerResolver idLayerResolver = InvestigatorUtils.getIdLayerResolver(investigatorId, true);
            List<Actor> actors = MapStage.getActor(idLayerResolver);
            if (actors.isEmpty()) {
                onSub.onCompleted();
                return;
            }
            actors.get(1).addAction(new FastForwardAction<>(Actions.sequence(
                    Actions.alpha(0, 1F),
                    Actions.run(() -> {
                        MapStage.removeActor(idLayerResolver);
                        MapStage.addDragAndZoomListeners();
                    })
            )));

            onSub.onCompleted();
        });

    }
}
