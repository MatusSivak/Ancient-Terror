package sk.sivak.eldritchhorror.core.view.action.focus;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import rx.Completable;
import sk.sivak.eldritchhorror.core.constants.ViewProperties;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.view.game.InfoStage;
import sk.sivak.eldritchhorror.core.view.game.MapStage;
import sk.sivak.eldritchhorror.core.view.map.clue.ClueUtils;
import sk.sivak.eldritchhorror.core.view.utils.FastForwardAction;
import sk.sivak.eldritchhorror.core.view.action.TokenSounds;

import java.util.List;

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
            Actor clueActor = nearestClueCopy(MapStage.getActor(clueIdLayerResolver), MapStage.getCamera().position);
            // Project the visible token's center before changing its origin, scale, or parent.
            Vector2 stageCoords = clueActor.localToStageCoordinates(new Vector2(clueActor.getWidth() / 2f, clueActor.getHeight() / 2f));
            Vector2 screenCoordinates = MapStage.getStage().stageToScreenCoordinates(stageCoords);
            Vector2 infoStageCoordinates = InfoStage.getStageSafe().screenToStageCoordinates(screenCoordinates);
            clueActor.clearActions();
            clueActor.clearListeners();
            clueActor.setOrigin(clueActor.getWidth() / 2f, clueActor.getHeight() / 2f);
            clueActor.setScale(clueActor.getScaleX() / MapStage.getCamera().zoom,
                    clueActor.getScaleY() / MapStage.getCamera().zoom);
            clueActor.setPosition(
                    infoStageCoordinates.x - clueActor.getWidth() / 2f,
                    infoStageCoordinates.y - clueActor.getHeight() / 2f);

            MapStage.removeActor(clueIdLayerResolver);

            InfoStage.getStageSafe().addActor(clueActor);

            withClue(clueActor, onSub);

        });

    }

    static Actor nearestClueCopy(List<Actor> copies, Vector3 cameraPosition) {
        Actor nearest = null;
        float nearestDistance = Float.POSITIVE_INFINITY;
        for (Actor copy : copies) {
            Vector2 center = copy.localToStageCoordinates(new Vector2(copy.getWidth() / 2f, copy.getHeight() / 2f));
            float distance = center.dst2(cameraPosition.x, cameraPosition.y);
            if (distance < nearestDistance) {
                nearest = copy;
                nearestDistance = distance;
            }
        }
        if (nearest == null) throw new IllegalStateException("No Clue token to collect");
        return nearest;
    }

    private static void withClue(Actor clueActor, rx.CompletableSubscriber onSub) {
        InfoStage.displayText(get("focus.gainingClue"));
        clueActor.setOrigin(clueActor.getWidth() / 2, clueActor.getHeight() / 2);
        float normalActionDuration = ViewProperties.NORMAL_ACTION_DURATION;

        Vector2 emptyContainerPosition = InfoStage.getInvestigatorHud().getClueBar().getEmptyContainerPosition(0);
        float targetScale = (VIEWPORT_HEIGHT * getMinSizeRatio()) / clueActor.getHeight();

        clueActor.addAction(new FastForwardAction<>(Actions.sequence(
                Actions.run(() -> TokenSounds.playGain(TokenSounds.Cue.CLUE_GAIN, 0)),
                Actions.parallel(
                        Actions.rotateTo(360, normalActionDuration, sine),
                        Actions.moveTo(
                                ViewProperties.VIEWPORT_WIDTH / 2f - clueActor.getWidth() / 2,
                                ViewProperties.VIEWPORT_HEIGHT / 2f - clueActor.getHeight() / 2,
                                normalActionDuration, sine),
                        Actions.scaleTo(4, 4, normalActionDuration, sine)
                ),
                Actions.parallel(
                        Actions.moveTo(emptyContainerPosition.x - (clueActor.getWidth() * targetScale)/2f - 7,
                                emptyContainerPosition.y - (clueActor.getHeight() * targetScale)/2f - 7, normalActionDuration, sine),
                        Actions.rotateTo(0, normalActionDuration, sine),
                        Actions.scaleTo(targetScale, targetScale, normalActionDuration, sine)
                ),
                Actions.run(() -> {
                    TokenSounds.playLand(TokenSounds.Cue.CLUE_GAIN, 0);
                    InfoStage.getInvestigatorHud().getClueBar().increaseCurrentValue();
                }),
                Actions.run(onSub::onCompleted),
                Actions.run(clueActor::remove)
        )));
    }

    private static float getMinSizeRatio() {
        return HUD_HEIGHT_RATIO * 0.5f;
    }


}
