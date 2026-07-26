package sk.sivak.eldritchhorror.core.view.components.track;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.ParallelAction;
import sk.sivak.eldritchhorror.core.view.bigactors.BigActorsManager;
import sk.sivak.eldritchhorror.core.view.game.HudButtons;

import static sk.sivak.eldritchhorror.core.constants.ViewProperties.VIEWPORT_HEIGHT;
import static sk.sivak.eldritchhorror.core.constants.ViewProperties.VIEWPORT_WIDTH;

public class MaxMinComponent {

    private final static float DURATION = 0.75f;
    private final float actorScaleX;
    private final float actorScaleY;
    private final float actorX;
    private final float actorY;
    private final BigActorsManager.BigActorKey bigActorKey;

    private boolean displayed = false;

    private Actor actor;

    public MaxMinComponent(Actor actor, BigActorsManager.BigActorKey bigActorKey) {
        this.actor = actor;
        this.bigActorKey = bigActorKey;
        this.actorScaleX = this.actor.getScaleX();
        this.actorScaleY = this.actor.getScaleY();
        this.actorX = this.actor.getX();
        this.actorY = this.actor.getY();
    }

    public void maximizeOrMinimize() {
        if (displayed) {
            minimize();
        } else {
            maximize();
        }
    }

    private void maximize() {
        float viewportHeightRatio = 0.65f;
        Vector2 stageCoordinates = actor.localToStageCoordinates(new Vector2());
        Vector2 difference = new Vector2(actor.getX(), actor.getY()).sub(stageCoordinates);
        float targetScale = (VIEWPORT_HEIGHT * viewportHeightRatio * actor.getScaleY()) / actor.getHeight();
        ParallelAction toCenterAction = Actions.parallel(
                Actions.scaleTo(targetScale, targetScale, DURATION),
                Actions.moveTo(difference.x + VIEWPORT_WIDTH / 2f - ((actor.getWidth() / actor.getScaleX()) * targetScale) / 2,
                        difference.y + VIEWPORT_HEIGHT / 2f - ((actor.getHeight() / actor.getScaleY()) * targetScale) / 2,
                        DURATION, Interpolation.sineOut)
        );
        actor.addAction(Actions.sequence(toCenterAction, Actions.run(this::afterMaximize)));
    }

    private void afterMaximize() {
        displayed = true;
        HudButtons.getInstance().uncheckAll();
        BigActorsManager.unlock(bigActorKey);
    }

    private void afterMinimize() {
        displayed = false;
        BigActorsManager.unlock(bigActorKey);
    }

    private void minimize() {
        ParallelAction toBackAction = Actions.parallel(
                Actions.scaleTo(actorScaleX, actorScaleY, DURATION),
                Actions.moveTo(actorX,
                        actorY,
                        DURATION, Interpolation.sineIn)
        );
        actor.addAction(Actions.sequence(toBackAction, Actions.run(this::afterMinimize)));
    }
}
