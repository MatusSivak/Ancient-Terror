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
    private Image holderImage;
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

        addAction(Actions.sequence(
                new FastForwardAction(reveal),
                new FastForwardAction(createHolderFlight(actionDuration)),
                Actions.run(() -> { remove(); onSub.onCompleted(); })
        ));
    }

    private TemporalAction createHolderFlight(float duration) {
        return new TemporalAction(duration) {
            private float startX, startY, scaleX, scaleY, rotation;
            private final Vector2 lower = new Vector2(), upper = new Vector2();

            @Override protected void begin() {
                startX = getX(); startY = getY(); scaleX = getScaleX(); scaleY = getScaleY(); rotation = getRotation();
            }

            @Override protected void update(float percent) {
                float targetX = holderLocation.x, targetY = holderLocation.y, targetScaleX = 1, targetScaleY = 1;
                if (holderImage != null) {
                    holderImage.validate();
                    holderImage.localToStageCoordinates(lower.set(holderImage.getImageX(), holderImage.getImageY()));
                    holderImage.localToStageCoordinates(upper.set(holderImage.getImageX() + holderImage.getImageWidth(),
                            holderImage.getImageY() + holderImage.getImageHeight()));
                    getParent().stageToLocalCoordinates(lower);
                    getParent().stageToLocalCoordinates(upper);
                    targetScaleX = (upper.x - lower.x) / getWidth();
                    targetScaleY = (upper.y - lower.y) / getHeight();
                    // Scaling happens around our origin, not around the destination's bottom-left corner.
                    targetX = lower.x - getOriginX() * (1 - targetScaleX);
                    targetY = lower.y - getOriginY() * (1 - targetScaleY);
                }
                float travel = Interpolation.sine.apply(percent);
                setPosition(MathUtils.lerp(startX, targetX, travel), MathUtils.lerp(startY, targetY, travel));
                setScale(MathUtils.lerp(scaleX, targetScaleX, percent), MathUtils.lerp(scaleY, targetScaleY, percent));
                setRotation(MathUtils.lerp(rotation, 360, travel));
            }
        };
    }

    public void setHolderImage(Image holderImage) {
        this.holderImage = holderImage;
    }

    public void setHolderLocation(Vector2 holderLocation) {
        this.holderLocation = holderLocation;
    }
}
