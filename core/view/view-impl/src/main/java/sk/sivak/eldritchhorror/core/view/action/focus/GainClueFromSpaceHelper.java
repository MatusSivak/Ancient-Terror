package sk.sivak.eldritchhorror.core.view.action.focus;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import rx.Completable;
import sk.sivak.eldritchhorror.core.constants.ViewProperties;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.view.game.InfoStage;
import sk.sivak.eldritchhorror.core.view.game.MapStage;
import sk.sivak.eldritchhorror.core.view.map.clue.ClueUtils;
import sk.sivak.eldritchhorror.core.view.utils.FastForwardAction;

import java.util.concurrent.TimeUnit;

import static com.badlogic.gdx.math.Interpolation.sine;
import static sk.sivak.eldritchhorror.core.constants.ViewProperties.HUD_HEIGHT_RATIO;
import static sk.sivak.eldritchhorror.core.constants.ViewProperties.VIEWPORT_HEIGHT;
import static sk.sivak.eldritchhorror.core.view.utils.UiText.get;

public final class GainClueFromSpaceHelper {

    private GainClueFromSpaceHelper() {
    }

    static Completable gainClueFromSpace(LocationId locationId) {
        return Completable.create(onSub -> {
            ClueUtils.ClueIdLayerResolver clueIdLayerResolver = new ClueUtils.ClueIdLayerResolver(locationId);
            Actor clueActor = MapStage.getActor(clueIdLayerResolver).get(1);
            clueActor.clearActions();
            clueActor.clearListeners();
            clueActor.setOrigin(0,0);


            Vector2 stageCoords = clueActor.localToStageCoordinates(new Vector2());
            Vector2 screenCoordinates = MapStage.getStage().stageToScreenCoordinates(stageCoords);
            Vector2 infoStageCoordinates = InfoStage.getStageSafe().screenToStageCoordinates(screenCoordinates);
            clueActor.setScale(1/ MapStage.getCamera().zoom);
            clueActor.setPosition(
                    infoStageCoordinates.x + (clueActor.getScaleX()-1) * clueActor.getWidth()/2f,
                    infoStageCoordinates.y  + (clueActor.getScaleY()-1) * clueActor.getHeight()/2f);

            MapStage.removeActor(clueIdLayerResolver);

            InfoStage.getStageSafe().addActor(clueActor);

            withClue(clueActor, onSub);

        });

    }

    private static void withClue(Actor clueActor, rx.CompletableSubscriber onSub) {
        InfoStage.displayText(get("focus.gainingClue"));
        clueActor.setOrigin(clueActor.getWidth() / 2, clueActor.getHeight() / 2);
        float normalActionDuration = ViewProperties.NORMAL_ACTION_DURATION;

        Vector2 emptyContainerPosition = InfoStage.getInvestigatorHud().getClueBar().getEmptyContainerPosition(0);
        float targetScale = (VIEWPORT_HEIGHT * getMinSizeRatio()) / clueActor.getHeight();

        clueActor.addAction(new FastForwardAction<>(Actions.sequence(
                Actions.parallel(
                        Actions.rotateTo(360, normalActionDuration, sine),
                        Actions.moveTo(
                                ViewProperties.VIEWPORT_WIDTH / 2f - clueActor.getWidth() / 2,
                                ViewProperties.VIEWPORT_HEIGHT / 2f - clueActor.getWidth() / 2,
                                normalActionDuration, sine),
                        Actions.scaleTo(4, 4, normalActionDuration, sine)
                ),
                Actions.parallel(
                        Actions.moveTo(emptyContainerPosition.x - (clueActor.getWidth() * targetScale)/2f,
                                emptyContainerPosition.y - (clueActor.getHeight() * targetScale)/2f, normalActionDuration, sine),
                        Actions.rotateTo(0, normalActionDuration, sine),
                        Actions.scaleTo(targetScale, targetScale, normalActionDuration, sine)
                ),
                Actions.run(() -> InfoStage.getInvestigatorHud().getClueBar().increaseCurrentValue()),
                Actions.run(onSub::onCompleted),
                Actions.run(clueActor::remove)
        )));
    }

    private static float getMinSizeRatio() {
        return HUD_HEIGHT_RATIO * 0.5f;
    }


}
