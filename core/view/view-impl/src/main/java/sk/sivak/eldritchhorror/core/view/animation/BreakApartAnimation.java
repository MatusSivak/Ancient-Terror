package sk.sivak.eldritchhorror.core.view.animation;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import rx.CompletableSubscriber;
import sk.sivak.eldritchhorror.core.constants.ViewProperties;
import sk.sivak.eldritchhorror.core.view.components.combat.SilhouetteImage;
import sk.sivak.eldritchhorror.core.view.utils.FastForwardAction;

/** A single clock keeps the zoom, recoil and debris synchronized at any fast-forward speed. */
public final class BreakApartAnimation extends Group {
    private static final float ZOOM_TIME = 0.72f;
    private static final float HOLD_TIME = 0.26f;
    private static final float BURST_TIME = 0.90f;
    private static final float TOTAL_TIME = ZOOM_TIME + HOLD_TIME + BURST_TIME;

    private final Actor subject;
    private final Image flash;
    private final float startX, startY, startScaleX, startScaleY, startAlpha, startRotation;
    private final float targetX, targetY, targetScale, extent;
    private TornApartActorImageBuilder.Fragments fragments;
    private final TextureShard[] pieces = new TextureShard[32];
    private final BurstLight burst;

    public static void start(Actor subject, Drawable flashDrawable, Color accent, String name, CompletableSubscriber subscriber) {
        Stage stage = subject.getStage();
        Vector2 center = subject.localToStageCoordinates(new Vector2(subject.getWidth() / 2, subject.getHeight() / 2));
        subject.clearActions();
        subject.clearListeners();
        subject.setTouchable(Touchable.disabled);
        subject.setOrigin(subject.getWidth() / 2, subject.getHeight() / 2);
        subject.setPosition(center.x - subject.getWidth() / 2, center.y - subject.getHeight() / 2);
        BreakApartAnimation animation = new BreakApartAnimation(subject, flashDrawable, accent, name);
        // The small-actor layer fades during other UI transitions; defeat owns its visibility.
        stage.addActor(animation);
        animation.addAction(new FastForwardAction<>(new TemporalAction(TOTAL_TIME * ViewProperties.NORMAL_ACTION_DURATION) {
            @Override protected void update(float percent) {
                animation.animate(percent * TOTAL_TIME);
            }
            @Override protected void end() {
                animation.remove();
                subscriber.onCompleted();
            }
        }));
    }

    private BreakApartAnimation(Actor subject, Drawable flashDrawable, Color accent, String name) {
        this.subject = subject;
        setName(name);
        setTransform(false);
        setTouchable(Touchable.disabled);
        startX = subject.getX();
        startY = subject.getY();
        startScaleX = subject.getScaleX();
        startScaleY = subject.getScaleY();
        startAlpha = subject.getColor().a;
        startRotation = subject.getRotation();
        targetX = ViewProperties.VIEWPORT_WIDTH / 2f - subject.getWidth() / 2;
        targetY = ViewProperties.VIEWPORT_HEIGHT / 2f - subject.getHeight() / 2;
        targetScale = presentationScale(subject.getWidth(), subject.getHeight());
        extent = Math.max(subject.getWidth(), subject.getHeight()) * targetScale;
        addActor(subject);
        burst = new BurstLight(accent, extent);
        burst.setPosition(ViewProperties.VIEWPORT_WIDTH / 2f, ViewProperties.VIEWPORT_HEIGHT / 2f);
        addActor(burst);
        flash = flashDrawable == null ? null : new SilhouetteImage(flashDrawable);
        if (flash != null) {
            flash.setName("defeat-flash");
            flash.setBounds(targetX, targetY, subject.getWidth(), subject.getHeight());
            flash.setOrigin(subject.getOriginX(), subject.getOriginY());
            flash.setColor(accent.r, accent.g, accent.b, 0f);
            addActor(flash);
        }
    }

    public static float presentationScale(float width, float height) {
        // Leave room for recoil and the outline around a much larger portrait.
        return Math.min(ViewProperties.VIEWPORT_WIDTH * 0.58f / width,
                ViewProperties.VIEWPORT_HEIGHT * 0.76f / height);
    }

    private void animate(float elapsed) {
        if (elapsed < ZOOM_TIME) {
            float progress = Interpolation.sineOut.apply(elapsed / ZOOM_TIME);
            subject.setPosition(MathUtils.lerp(startX, targetX, progress), MathUtils.lerp(startY, targetY, progress));
            subject.setScale(MathUtils.lerp(startScaleX, targetScale, progress),
                    MathUtils.lerp(startScaleY, targetScale, progress));
            subject.setRotation(MathUtils.lerp(startRotation, 0f, progress) - 8f * MathUtils.sin(progress * MathUtils.PI));
            subject.getColor().a = MathUtils.lerp(startAlpha, 1f, Math.min(elapsed / 0.22f, 1f));
        } else if (elapsed < ZOOM_TIME + HOLD_TIME) {
            float progress = (elapsed - ZOOM_TIME) / HOLD_TIME;
            float pulse = MathUtils.sin(progress * MathUtils.PI);
            subject.setPosition(targetX, targetY);
            subject.setScale(targetScale * (1f + 0.035f * pulse));
            subject.setRotation(2f * MathUtils.sin(progress * MathUtils.PI * 4f) * pulse);
            subject.getColor().a = 1f;
            if (flash != null) {
                flash.setScale(subject.getScaleX());
                flash.setRotation(subject.getRotation());
                flash.getColor().a = 0.72f * progress;
            }
        } else {
            if (fragments == null) split();
            float progress = Math.min((elapsed - ZOOM_TIME - HOLD_TIME) / BURST_TIME, 1f);
            float travel = Interpolation.pow3Out.apply(progress);
            // Preserve readable pieces briefly, then fade instead of immediately shrinking to zero.
            float opacity = 1f - Interpolation.pow2In.apply(MathUtils.clamp((progress - 0.12f) / 0.88f, 0f, 1f));
            float paddedWidth = subject.getWidth() * TornApartActorImageBuilder.PADDED_SIZE * targetScale;
            float paddedHeight = subject.getHeight() * TornApartActorImageBuilder.PADDED_SIZE * targetScale;
            for (int i = 0; i < pieces.length; i++) {
                TextureShard piece = pieces[i];
                float dx = piece.getOriginX() - paddedWidth / 2, dy = piece.getOriginY() - paddedHeight / 2;
                float distance = Math.max(1, Vector2.len(dx, dy));
                float speed = extent * (.40f + (i % 5) * .075f);
                piece.setPosition(ViewProperties.VIEWPORT_WIDTH / 2f - paddedWidth / 2 + dx / distance * speed * travel,
                        ViewProperties.VIEWPORT_HEIGHT / 2f - paddedHeight / 2 + dy / distance * speed * travel
                                - extent * .22f * progress * progress);
                piece.setScale(1f - .55f * progress, 1f - .30f * progress);
                piece.setRotation((i % 2 == 0 ? -1 : 1) * (70f + i % 7 * 23f) * travel);
                pieces[i].getColor().a = opacity;
            }
            if (flash != null) {
                flash.setRotation(0f);
                flash.setScale(targetScale * (1f + 0.18f * travel));
                flash.getColor().a = 0.72f * Math.max(0f, 1f - progress * 7f);
            }
            burst.progress(progress);
        }
    }

    private void split() {
        subject.setPosition(targetX, targetY);
        subject.setScale(targetScale);
        subject.setRotation(0f);
        subject.getColor().a = 1f;
        fragments = TornApartActorImageBuilder.build(subject);
        subject.setVisible(false);
        float[][] triangles = ShatterGeometry.triangles(4, 4);
        for (int i = 0; i < pieces.length; i++) {
            TextureShard piece = new TextureShard(fragments.texture(),
                    subject.getWidth() * TornApartActorImageBuilder.PADDED_SIZE * targetScale,
                    subject.getHeight() * TornApartActorImageBuilder.PADDED_SIZE * targetScale, triangles[i], true);
            piece.setName("defeat-fragment-" + i);
            addActor(piece);
            pieces[i] = piece;
        }
        burst.toFront();
    }

    @Override protected void setStage(Stage stage) {
        super.setStage(stage);
        if (stage == null) {
            // Also release GPU snapshots when the screen closes partway through the burst.
            clearActions();
            if (fragments != null) fragments.dispose();
        }
    }
}
