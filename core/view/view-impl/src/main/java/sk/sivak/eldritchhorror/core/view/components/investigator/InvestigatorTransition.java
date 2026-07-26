package sk.sivak.eldritchhorror.core.view.components.investigator;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import rx.Completable;
import rx.CompletableSubscriber;
import sk.sivak.eldritchhorror.core.constants.ViewProperties;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager;
import sk.sivak.eldritchhorror.core.view.game.MapStage;
import sk.sivak.eldritchhorror.core.view.map.helper.DistanceHelper;
import sk.sivak.eldritchhorror.core.view.map.investigator.InvestigatorUtils;
import sk.sivak.eldritchhorror.core.view.utils.FastForwardAction;

import java.util.List;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.alpha;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.moveTo;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.parallel;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.run;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.scaleTo;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;
import static sk.sivak.eldritchhorror.core.constants.ViewProperties.VIEWPORT_HEIGHT;

public class InvestigatorTransition extends Group {

    public static final int BORDER = 15;
    private final Image image;
    private InvestigatorId investigatorId;

    private InvestigatorTransition(Texture texture, InvestigatorId investigatorId) {
        this.investigatorId = investigatorId;
        image = new Image(texture);
        image.setWidth(762);
        image.setHeight(853);
        setWidth(image.getWidth());
        setHeight(image.getHeight());
        setScale(0f);
        setOrigin(getWidth() / 2, getHeight() / 2);
        setPosition(ViewProperties.VIEWPORT_WIDTH / 2 - getWidth() / 2,
                0 - getHeight());
        addActor(image);
    }

    public static InvestigatorTransition build(InvestigatorId investigatorId) {
        return new InvestigatorTransition(CustomAssetManager.getInvestigatorTexture(investigatorId), investigatorId);
    }

    public Completable display(Action actionToRun) {
        return Completable.create(onSub -> display(onSub, actionToRun));
    }

    private void display(CompletableSubscriber onSub, Action actionToRun) {
        if (actionToRun == null) {
            actionToRun = new FastForwardAction(Actions.delay(0.25f));
        }
        float targetScale = (ViewProperties.VIEWPORT_WIDTH * 0.30f) / getWidth();
        float targetScale2 = (ViewProperties.VIEWPORT_WIDTH * 90/853f) / getWidth();
        addAction(
                sequence(
                        new FastForwardAction(parallel(
                                moveTo(ViewProperties.VIEWPORT_WIDTH / 2 - getWidth() / 2,
                                        VIEWPORT_HEIGHT / 2 - getHeight() / 2,
                                        1f, Interpolation.sine),
                                scaleTo(targetScale, targetScale, 1f, Interpolation.sine)
                        )),
                        actionToRun,
                        run(() -> onSub.onCompleted()),
                        new FastForwardAction(parallel(
                                scaleTo(targetScale2, targetScale2, 2, Interpolation.sine),
                                new DynamicMoveToAction(2, Interpolation.sine),
                                alpha(0, 2f, Interpolation.sineIn)
                        )),
                        run(this::remove)
                )
        );
    }

    private class DynamicMoveToAction extends MoveToAction {

        public DynamicMoveToAction(float duration, Interpolation interpolation) {
            setDuration(duration);
            setInterpolation(interpolation);
        }

        @Override
        public boolean act(float delta) {
            List<Actor> allActors = MapStage.getActor(InvestigatorUtils.getIdLayerResolver(investigatorId, false));
            int indexOfClosestActor = findIndexOfClosestActor(allActors);
            Actor actor = allActors.get(indexOfClosestActor);
            Vector2 stageCoords = actor.localToStageCoordinates(new Vector2(actor.getWidth()/2,actor.getHeight()/2));

            Vector2 coordinates = MapStage.getStage().getViewport().project(stageCoords);
            coordinates.sub(new Vector2(
                    MapStage.getStage().getViewport().getLeftGutterWidth(),
                    MapStage.getStage().getViewport().getBottomGutterHeight()));
            coordinates.y = getStage().getViewport().getScreenHeight() + getStage().getViewport().getBottomGutterHeight() - coordinates.y; // ???
            coordinates.x += getStage().getViewport().getLeftGutterWidth();


            Vector2 vector2 = getStage().screenToStageCoordinates(coordinates);

            setPosition(vector2.x - InvestigatorTransition.this.getWidth()/2f,
                    vector2.y - InvestigatorTransition.this.getHeight()/2);
            return super.act(delta);
        }
    }

    private int findIndexOfClosestActor(List<Actor> allActors) {
        int index = 0;
        Float minDistanceToActor = null;
        for (int i = 0; i < allActors.size(); i++) {
            float distance = (float) DistanceHelper.getDistance(new Vector2(allActors.get(i).getX(), allActors.get(i).getY()), MapStage.getCamera().position);
            if (minDistanceToActor == null || distance < minDistanceToActor) {
                minDistanceToActor = distance;
                index = i;
            }
        }
        return index;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        Vector2 vector2 = localToStageCoordinates(new Vector2());
        batch.setColor(0, 1, 0, parentAlpha * getColor().a / 2);
        batch.draw(CustomAssetManager.getTexture(CustomAssetManager.INVESTIGATOR_BORDER),
                vector2.x - BORDER * getScaleX(),
                vector2.y - BORDER * getScaleY(),
                (getWidth() + BORDER * 2) * getScaleX(),
                (getHeight() + BORDER * 2) * getScaleY());
        super.draw(batch, parentAlpha);

    }
}
