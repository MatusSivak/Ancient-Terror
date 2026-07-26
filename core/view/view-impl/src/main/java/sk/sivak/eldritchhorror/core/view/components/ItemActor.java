package sk.sivak.eldritchhorror.core.view.components;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.actions.*;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import rx.Completable;
import rx.CompletableSubscriber;
import sk.sivak.eldritchhorror.core.constants.ViewProperties;
import sk.sivak.eldritchhorror.core.view.utils.FastForwardAction;

import static sk.sivak.eldritchhorror.core.constants.ViewProperties.*;

/**
 * @author msivak
 */
public class ItemActor extends Image {

    protected Vector2 holderLocation = new Vector2(0, 0);

    public ItemActor(Texture texture) {
        super(texture);
        init();
    }

    ItemActor() {
        init();
    }

    protected void init() {
        float scale = (VIEWPORT_HEIGHT * getMinSizeRatio()) / getHeight();
        setWidth(getWidth() * scale);
        setHeight(getHeight() * scale);
        setOrigin(getWidth() / 2, getHeight() / 2);
    }

    private float getMinSizeRatio() {
        return HUD_HEIGHT_RATIO * 0.5f;
    }

    public Completable gain() {
        getColor().a = 0f;
        return Completable.create(this::gain);
    }

    private void gain(CompletableSubscriber onSub) {
        float actionDuration = ViewProperties.NORMAL_ACTION_DURATION;
        setX(VIEWPORT_WIDTH / 2);
        setY(VIEWPORT_HEIGHT / 2);

        ScaleToAction scaleUpAction = new ScaleToAction();
        scaleUpAction.setScale(4f);
        scaleUpAction.setDuration(actionDuration);
        scaleUpAction.setInterpolation(Interpolation.swingOut);
        scaleUpAction.setActor(this);

        AlphaAction fadeInAction = new AlphaAction();
        fadeInAction.setAlpha(1f);
        fadeInAction.setDuration(actionDuration);
        fadeInAction.setActor(this);

        MoveToAction moveToHolderAction = new MoveToAction();
        moveToHolderAction.setPosition(holderLocation.x, holderLocation.y);
        moveToHolderAction.setDuration(actionDuration);
        scaleUpAction.setInterpolation(Interpolation.sine);
        moveToHolderAction.setActor(this);

        RotateToAction rotateToAction = new RotateToAction();
        rotateToAction.setRotation(360);
        rotateToAction.setDuration(actionDuration);
        scaleUpAction.setInterpolation(Interpolation.sine);
        rotateToAction.setActor(this);

        ScaleToAction scaleDownAction = new ScaleToAction();
        scaleDownAction.setScale(1f);
        scaleDownAction.setDuration(actionDuration);
        scaleDownAction.setActor(this);

        addAction(Actions.sequence(
                new FastForwardAction(Actions.parallel(
                        fadeInAction,
                        scaleUpAction
                )),
                new FastForwardAction(Actions.parallel(
                        moveToHolderAction,
                        rotateToAction,
                        scaleDownAction
                )),
                Actions.run(onSub::onCompleted),
                Actions.run(this::remove)
        ));
    }

    public void setHolderLocation(Vector2 holderLocation) {
        this.holderLocation = holderLocation;
    }
}
