package sk.sivak.eldritchhorror.core.view.components;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.actions.*;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import rx.Completable;
import rx.CompletableSubscriber;
import sk.sivak.eldritchhorror.core.constants.ViewProperties;
import sk.sivak.eldritchhorror.core.view.animation.TokenMaterializeEffect;
import sk.sivak.eldritchhorror.core.view.utils.FastForwardAction;

import static sk.sivak.eldritchhorror.core.constants.ViewProperties.*;

/**
 * @author msivak
 */
public class ItemActor extends Image {

    protected Vector2 holderLocation = new Vector2(0, 0);
    private final Color materializeColor = new Color(.95f, .77f, .4f, 1);
    private TokenMaterializeEffect materializeEffect;
    private float materializeProgress = 1;

    public void setMaterializeColor(Color color) {
        materializeColor.set(color);
    }

    @Override public void draw(Batch batch, float parentAlpha) {
        if (materializeEffect != null && materializeProgress < 1) {
            materializeEffect.draw(batch, getX() + getOriginX(), getY() + getOriginY(),
                    Math.max(getWidth(), getHeight()) * 4, materializeProgress, parentAlpha);
        }
        super.draw(batch, parentAlpha);
    }

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
        setPosition(VIEWPORT_WIDTH / 2 - getOriginX(), VIEWPORT_HEIGHT / 2 - getOriginY());
        materializeEffect = new TokenMaterializeEffect(materializeColor);
        materializeProgress = 0;
        setScale(2.5f, .7f);
        setRotation(-28);
        TemporalAction reveal = new TemporalAction(actionDuration) {
            @Override protected void update(float percent) {
                materializeProgress = percent;
                float form = MathUtils.clamp((percent - .12f) / .65f, 0, 1);
                float eased = Interpolation.swingOut.apply(form);
                setScale(MathUtils.lerp(2.5f, 4, eased), MathUtils.lerp(.7f, 4, eased));
                setRotation(-28 * (1 - Interpolation.pow3Out.apply(form)));
                getColor().a = MathUtils.clamp((percent - .1f) / .4f, 0, 1);
            }
            @Override protected void end() {
                setScale(4); setRotation(0); getColor().a = 1;
                materializeProgress = 1;
                materializeEffect = null;
            }
        };

        MoveToAction moveToHolderAction = new MoveToAction();
        moveToHolderAction.setPosition(holderLocation.x, holderLocation.y);
        moveToHolderAction.setDuration(actionDuration);
        moveToHolderAction.setInterpolation(Interpolation.sine);
        moveToHolderAction.setActor(this);

        RotateToAction rotateToAction = new RotateToAction();
        rotateToAction.setRotation(360);
        rotateToAction.setDuration(actionDuration);
        rotateToAction.setInterpolation(Interpolation.sine);
        rotateToAction.setActor(this);

        ScaleToAction scaleDownAction = new ScaleToAction();
        scaleDownAction.setScale(1f);
        scaleDownAction.setDuration(actionDuration);
        scaleDownAction.setActor(this);

        addAction(Actions.sequence(
                new FastForwardAction(reveal),
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
