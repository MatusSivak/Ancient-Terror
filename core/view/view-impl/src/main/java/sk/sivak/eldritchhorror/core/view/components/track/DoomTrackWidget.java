package sk.sivak.eldritchhorror.core.view.components.track;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import rx.functions.Action0;
import sk.sivak.eldritchhorror.core.view.utils.FastForwardAction;

import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.*;

public class DoomTrackWidget extends Group {

    private static final Vector2 CLOCK_ORIGIN = new Vector2(297, 297);
    private static final Vector2 MINUTE_HAND_ORIGIN = new Vector2(24, 24);
    private static final Vector2 HOUR_HAND_ORIGIN = new Vector2(35, 31);
    private static final Vector2 KNOB_ORIGIN = new Vector2(42, 36);
    private static final float KNOB_SCALE = 0.5f;
    private static final float MINUTE_MIN_ROTATION = 30f;
    private static final float HOUR_MIN_ROTATION = 2.5f;
    private final Image hourHand;
    private final Image minuteHand;
    private int currentDoom;
    private Action0 onCompletedListener;

    public DoomTrackWidget() {
        Image clock = new Image(getTextureRegionDrawable(CLOCK));
        minuteHand = new Image(getTextureRegionDrawable(CLOCK_MINUTE_HAND));
        hourHand = new Image(getTextureRegionDrawable(CLOCK_HOUR_HAND));
        Image knob = new Image(getTextureRegionDrawable(CLOCK_KNOB));

        minuteHand.setTouchable(Touchable.disabled);
        hourHand.setTouchable(Touchable.disabled);
        knob.setTouchable(Touchable.disabled);

        clock.setOrigin(CLOCK_ORIGIN.x, CLOCK_ORIGIN.y);
        minuteHand.setOrigin(MINUTE_HAND_ORIGIN.x, MINUTE_HAND_ORIGIN.y);
        hourHand.setOrigin(HOUR_HAND_ORIGIN.x, HOUR_HAND_ORIGIN.y);

        knob.setScale(KNOB_SCALE);

        minuteHand.setPosition(CLOCK_ORIGIN.x - MINUTE_HAND_ORIGIN.x, CLOCK_ORIGIN.y - MINUTE_HAND_ORIGIN.y);
        hourHand.setPosition(CLOCK_ORIGIN.x - HOUR_HAND_ORIGIN.x, CLOCK_ORIGIN.y - HOUR_HAND_ORIGIN.y);
        knob.setPosition(CLOCK_ORIGIN.x - KNOB_ORIGIN.x * KNOB_SCALE, CLOCK_ORIGIN.y - KNOB_ORIGIN.y * KNOB_SCALE);

        addActor(clock);
        addActor(minuteHand);
        addActor(hourHand);
        addActor(knob);

        setScale(67 / 593f);
    }

    public void updateDoom(int newDoom) {
        if (newDoom < 0) {
            newDoom = 0;
        }
        float fiveMinuteRotationDuration = 1.5f;
        if (currentDoom == 0) {
            fiveMinuteRotationDuration = 0.1f;
        }
        float actionDuration = Math.abs(currentDoom - newDoom) * fiveMinuteRotationDuration;
        if (actionDuration == 0) {
            return;
        }
        minuteHand.addAction(new FastForwardAction<>(Actions.rotateBy((newDoom - currentDoom) * MINUTE_MIN_ROTATION,
                actionDuration,
                Interpolation.linear)));
        hourHand.addAction(new FastForwardAction<>(Actions.rotateBy((newDoom - currentDoom) * HOUR_MIN_ROTATION,
                actionDuration,
                Interpolation.linear)));

        if (onCompletedListener != null) {
            addAction(new FastForwardAction<>(Actions.sequence(
                    Actions.delay(actionDuration),
                    Actions.run(() -> onCompletedListener.call()))));
        }
        this.currentDoom = newDoom;
    }

    public void addOnCompletedListener(Action0 onCompletedListener) {
        this.onCompletedListener = onCompletedListener;
    }

    public int getCurrentDoom() {
        return currentDoom;
    }

    @Override
    public float getWidth() {
        return 594 * getScaleX();
    }

    @Override
    public float getHeight() {
        return 593 * getScaleY();
    }
}
