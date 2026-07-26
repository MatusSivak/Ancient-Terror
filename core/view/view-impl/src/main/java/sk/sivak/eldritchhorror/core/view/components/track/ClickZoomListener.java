package sk.sivak.eldritchhorror.core.view.components.track;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.ParallelAction;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import rx.functions.Action0;
import sk.sivak.eldritchhorror.core.view.utils.FastForwardAction;

import java.util.LinkedList;
import java.util.List;

import static sk.sivak.eldritchhorror.core.constants.ViewProperties.VIEWPORT_HEIGHT;
import static sk.sivak.eldritchhorror.core.constants.ViewProperties.VIEWPORT_WIDTH;

public class ClickZoomListener extends ClickListener {

    private final static float DURATION = 0.75f;
    private List<ClickZoomListener> otherZoomListeners = new LinkedList<>();
    private Actor actor;
    private boolean moving = false;
    private boolean minimized = true;
    private float actorScaleY;
    private float actorScaleX;
    private float actorX;
    private float actorY;
    private Action0 onCompletedListener;

    public ClickZoomListener(Actor actor) {
        this.actor = actor;
    }

    public void setOnCompletedListener(Action0 onCompletedListener) {
        this.onCompletedListener = onCompletedListener;
    }

    public void addOtherZoomListener(ClickZoomListener otherZoomListener) {
        otherZoomListeners.add(otherZoomListener);
    }

    @Override
    public void clicked(InputEvent event, float x, float y) {
        if (moving) {
            return;
        }
        for (ClickZoomListener otherZoomListener : otherZoomListeners) {
            if (otherZoomListener.isMoving()) {
                return;
            }
        }
        if (minimized) {
            for (ClickZoomListener otherZoomListener : otherZoomListeners) {
                if (!otherZoomListener.isMinimized()) {
                    otherZoomListener.minimize();
                }
            }
            maximize();
        } else {
            minimize();
        }

    }

    public void maximize() {
        moving = true;
        minimized = false;
        float viewportHeightRatio = 0.65f;
        actorScaleY = actor.getScaleY();
        actorScaleX = actor.getScaleX();
        actorX = actor.getX();
        actorY = actor.getY();

        Vector2 stageCoordinates = actor.localToStageCoordinates(new Vector2());
        Vector2 difference = new Vector2(actorX, actorY).sub(stageCoordinates);
        float targetScale = (VIEWPORT_HEIGHT * viewportHeightRatio * actorScaleY) / actor.getHeight();
        ParallelAction toCenterAction = Actions.parallel(
                Actions.scaleTo(targetScale, targetScale, DURATION),
                Actions.moveTo(difference.x + VIEWPORT_WIDTH / 2 - ((actor.getWidth() / actorScaleX) * targetScale) / 2,
                        difference.y + VIEWPORT_HEIGHT / 2 - ((actor.getHeight() / actorScaleY) * targetScale) / 2,
                        DURATION, Interpolation.sineOut)
        );

        if (onCompletedListener != null) {
            actor.addAction(Actions.sequence(
                    new FastForwardAction(toCenterAction),
                    Actions.run(() -> moving = false),
                    Actions.run(() -> onCompletedListener.call())
                    ));
        } else {
            actor.addAction(Actions.sequence(new FastForwardAction(toCenterAction), Actions.run(() -> moving = false)));
        }
    }

    public void minimize() {
        moving = true;
        minimized = true;
        ParallelAction toBackAction = Actions.parallel(
                Actions.scaleTo(actorScaleX, actorScaleY, DURATION),
                Actions.moveTo(actorX,
                        actorY,
                        DURATION, Interpolation.sineIn)
        );
        if (onCompletedListener != null) {
            actor.addAction(Actions.sequence(
                    new FastForwardAction(toBackAction),
                    Actions.run(() -> moving = false),
                    Actions.run(() -> onCompletedListener.call())
                    ));
        } else {
            actor.addAction(Actions.sequence(new FastForwardAction(toBackAction), Actions.run(() -> moving = false)));
        }
    }

    public boolean isMoving() {
        return moving;
    }

    public boolean isMinimized() {
        return minimized;
    }
}
