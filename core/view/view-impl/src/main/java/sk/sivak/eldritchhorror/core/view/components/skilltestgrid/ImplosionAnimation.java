package sk.sivak.eldritchhorror.core.view.components.skilltestgrid;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.utils.Array;

/**
 * Plays a single-shot implosion sprite-sheet effect centered on a fixed position.
 * All 16 frames are rendered into the same destination rectangle — the shrinking
 * effect is baked into the sheet, not applied programmatically.
 */
public class ImplosionAnimation {

    static final float FRAME_DURATION = 0.04f;
    static final float DEFAULT_END_SCALE = 1f;
    static final float DEFAULT_OVERLAY_START_ALPHA = 1f;
    static final float DEFAULT_OVERLAY_END_ALPHA = 0f;

    private final Animation<TextureRegion> animation;
    private final Animation<TextureRegion> overlayAnimation;
    private float stateTime = 0f;
    private boolean finished = false;

    private final float centerX;
    private final float centerY;
    private final float width;
    private final float height;
    private final float endScale;
    private final Interpolation scaleInterpolation;
    private final float startAlpha;
    private final float endAlpha;
    private final float overlayStartAlpha;
    private final float overlayEndAlpha;

    /**
     * @param frames   shared 16-frame array (row-major order, not owned by this instance)
     * @param centerX  x center in the parent group's local coordinate space
     * @param centerY  y center in the parent group's local coordinate space
     * @param size     width and height of the destination rectangle
     */
    ImplosionAnimation(Array<TextureRegion> frames, float centerX, float centerY, float size) {
        this(frames, null, centerX, centerY, size, DEFAULT_END_SCALE,
                1f, 1f, DEFAULT_OVERLAY_START_ALPHA, DEFAULT_OVERLAY_END_ALPHA, null);
    }

    ImplosionAnimation(Array<TextureRegion> frames, float centerX, float centerY, float size, float endScale) {
        this(frames, null, centerX, centerY, size, endScale,
                1f, 1f, DEFAULT_OVERLAY_START_ALPHA, DEFAULT_OVERLAY_END_ALPHA, null);
    }

    ImplosionAnimation(Array<TextureRegion> frames, Array<TextureRegion> overlayFrames, float centerX, float centerY, float size, float endScale) {
        this(frames, overlayFrames, centerX, centerY, size, endScale,
                1f, 1f, DEFAULT_OVERLAY_START_ALPHA, DEFAULT_OVERLAY_END_ALPHA, null);
    }

    ImplosionAnimation(Array<TextureRegion> frames, Array<TextureRegion> overlayFrames,
                       float centerX, float centerY, float size, float endScale,
                       float startAlpha, float endAlpha,
                       float overlayStartAlpha, float overlayEndAlpha,
                       Interpolation scaleInterpolation) {
        this.animation = new Animation<>(FRAME_DURATION, frames, Animation.PlayMode.NORMAL);
        this.overlayAnimation = overlayFrames == null || overlayFrames.size == 0
                ? null
                : new Animation<>(FRAME_DURATION, overlayFrames, Animation.PlayMode.NORMAL);
        this.centerX = centerX;
        this.centerY = centerY;
        this.width = size;
        this.height = size;
        this.endScale = endScale;
        this.scaleInterpolation = scaleInterpolation;
        this.startAlpha = startAlpha;
        this.endAlpha = endAlpha;
        this.overlayStartAlpha = overlayStartAlpha;
        this.overlayEndAlpha = overlayEndAlpha;
    }

    void update(float delta) {
        if (finished) return;
        stateTime += delta;
        if (animation.isAnimationFinished(stateTime)) {
            finished = true;
        }
    }

    void draw(Batch batch, float parentAlpha) {
        if (finished) return;
        float progress = Math.min(stateTime / animation.getAnimationDuration(), 1f);
        float scaleProgress = scaleInterpolation == null ? progress : scaleInterpolation.apply(progress);
        float scale = 1f + (endScale - 1f) * scaleProgress;
        float drawWidth = width * scale;
        float drawHeight = height * scale;
        float batchRed = batch.getColor().r;
        float batchGreen = batch.getColor().g;
        float batchBlue = batch.getColor().b;
        float batchAlpha = batch.getColor().a;

        if (overlayAnimation != null) {
            TextureRegion overlayFrame = overlayAnimation.getKeyFrame(stateTime, false);
            float alphaProgress = overlayStartAlpha < overlayEndAlpha
                    ? Interpolation.sineOut.apply(progress)
                    : progress;
            float overlayAlpha = overlayStartAlpha + (overlayEndAlpha - overlayStartAlpha) * alphaProgress;
            batch.setColor(batchRed, batchGreen, batchBlue, batchAlpha * overlayAlpha);
            batch.draw(overlayFrame, centerX - drawWidth / 2f, centerY - drawHeight / 2f, drawWidth, drawHeight);
            batch.setColor(batchRed, batchGreen, batchBlue, batchAlpha);
        }

        TextureRegion frame = animation.getKeyFrame(stateTime, false);
        float baseAlpha = startAlpha + (endAlpha - startAlpha) * progress;
        batch.setColor(batchRed, batchGreen, batchBlue, batchAlpha * baseAlpha);
        batch.draw(frame, centerX - drawWidth / 2f, centerY - drawHeight / 2f, drawWidth, drawHeight);
        batch.setColor(batchRed, batchGreen, batchBlue, batchAlpha);
    }

    boolean isFinished() {
        return finished;
    }
}
