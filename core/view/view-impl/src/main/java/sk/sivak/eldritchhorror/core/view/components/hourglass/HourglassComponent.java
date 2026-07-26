package sk.sivak.eldritchhorror.core.view.components.hourglass;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.RepeatAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager;

public class HourglassComponent extends Image {

    private final Color parentColor;
    private RepeatAction loopAnimationAction;
    private float internalScale;

    public HourglassComponent(Color parentColor) {
        super(CustomAssetManager.getTexture(CustomAssetManager.HOURGLASS));
        this.parentColor = parentColor;
        setInternalScale(0.4f);
        setScale(0f);
        setColor(new Color(1f, 1f, 1f, 0f));
    }

    public void setInternalScale(float internalScale) {
        this.internalScale = internalScale;
        setWidth(500 * internalScale);
        setHeight(750 * internalScale);
        setOrigin((500 * internalScale) / 2f, (750 * internalScale) / 2f);
    }

    public void display() {
        addAction(
                Actions.sequence(
                        Actions.parallel(
                                Actions.scaleTo(1f, 1f, 1f, Interpolation.swingOut),
                                Actions.alpha(1f, 1f)
                        ),
                        Actions.run(this::loopAnimation)
                )
        );
    }

    public void hide() {
        if (loopAnimationAction != null) {
            loopAnimationAction.finish();
        }
        addAction(
                Actions.sequence(
                        Actions.parallel(
                                Actions.scaleTo(0f, 0f, 1f, Interpolation.swingIn),
                                Actions.alpha(0f, 1f)
                        ),
                        Actions.run(this::remove)
                )
        );
    }

    private void loopAnimation() {
        this.loopAnimationAction = Actions.repeat(RepeatAction.FOREVER,
                Actions.sequence(
                        Actions.repeat(4,
                                Actions.sequence(
                                        Actions.moveBy(-3, 0, 0.05f, Interpolation.sine),
                                        Actions.moveBy(6, 0, 0.1f, Interpolation.sine),
                                        Actions.moveBy(-3, 0, 0.05f, Interpolation.sine)
                                )
                        ),
                        Actions.parallel(
                                Actions.moveBy(0, 100 * internalScale, 0.25f, Interpolation.sine),
                                Actions.scaleTo(1.25f, 1.25f, 0.25f, Interpolation.sine)
                        ),
                        Actions.rotateBy(-360, 0.75f, Interpolation.sine),
                        Actions.parallel(
                                Actions.moveBy(0, -100 * internalScale, 0.25f, Interpolation.sine),
                                Actions.scaleTo(1f, 1f, 0.25f, Interpolation.sine)
                        )

                )
        );
        addAction(loopAnimationAction);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        float alpha = getColor().a;
        getColor().a *= parentColor.a;
        super.draw(batch, parentAlpha);
        getColor().a = alpha;

    }
}
