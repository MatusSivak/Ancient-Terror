package sk.sivak.eldritchhorror.core.view.components;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.actions.*;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import sk.sivak.eldritchhorror.core.constants.ViewProperties;

import static sk.sivak.eldritchhorror.core.constants.ViewProperties.VIEWPORT_HEIGHT;

/**
 * @author msivak
 */
public class CustomButton extends ImageButton {

    public CustomButton(Drawable imageUp, Drawable imageDown) {
        super(imageUp, imageDown);
        float scale = (VIEWPORT_HEIGHT * 0.15f) / getHeight();
        setWidth(getWidth() * scale);
        setHeight(getHeight() * scale);

        getImage().setOrigin(getWidth() / 2, getHeight() / 2);

        RotateToAction rotateLeftAction = createRotateAction(getLeftRotationAngle());
        RotateToAction rotateRightAction = createRotateAction(getRightRotationAngle());

        ScaleToAction zoomInAction = createScaleAction(1);
        ScaleToAction zoomOutAction = createScaleAction(0.8f);

        getImage().addAction(Actions.repeat(RepeatAction.FOREVER, new SequenceAction(
                rotateLeftAction, rotateRightAction
        )));

        getImage().addAction(Actions.repeat(RepeatAction.FOREVER, new SequenceAction(
                zoomInAction, zoomOutAction
        )));
    }

    protected int getLeftRotationAngle() {
        return -15;
    }

    protected int getRightRotationAngle() {
        return 15;
    }

    private RotateToAction createRotateAction(float rotation) {
        RotateToAction rotateLeftAction = new RotateToAction();
        rotateLeftAction.setActor(getImage());
        rotateLeftAction.setInterpolation(Interpolation.sine);
        rotateLeftAction.setDuration(ViewProperties.FADING_EFFECT_DURATION);
        rotateLeftAction.setRotation(rotation);
        return rotateLeftAction;
    }

    private ScaleToAction createScaleAction(float scale) {
        ScaleToAction scaleToAction = new ScaleToAction();
        scaleToAction.setActor(getImage());
        scaleToAction.setInterpolation(Interpolation.sine);
        scaleToAction.setDuration(ViewProperties.FADING_EFFECT_DURATION / 2f);
        scaleToAction.setScale(scale);
        return scaleToAction;
    }

}
