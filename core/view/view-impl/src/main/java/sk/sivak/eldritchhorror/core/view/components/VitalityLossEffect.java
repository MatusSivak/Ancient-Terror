package sk.sivak.eldritchhorror.core.view.components;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction;
import rx.Completable;
import rx.functions.Action0;
import sk.sivak.eldritchhorror.core.view.utils.FastForwardAction;

import static sk.sivak.eldritchhorror.core.constants.ViewProperties.*;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.*;

/** A token lifts out of its slot, swells under strain, then fractures with a coloured impact. */
public final class VitalityLossEffect extends Actor {
    public enum Kind { HEALTH, SANITY, MONSTER }

    private static final float LIFT = .48f, STRAIN = .20f, BREAK = .78f;
    private final TextureRegion left, right, pixel;
    private final Texture halo;
    private final Kind kind;
    private final Color accent, savedColor = new Color();
    private final float startX, startY, targetX, targetY, initialHeight, aspect;
    private float time, centerX, centerY, height, rotation;

    public static Completable play(Stage stage, Vector2 position, float width, float height, float offsetX,
                                   Kind kind, Action0 onLeave, Action0 onTear) {
        return Completable.create(subscriber -> {
            VitalityLossEffect effect = new VitalityLossEffect(position, width, height, offsetX, kind);
            stage.addActor(effect);
            if (onLeave != null) onLeave.call();
            effect.addAction(new FastForwardAction<>(new TemporalAction((LIFT + STRAIN + BREAK) * NORMAL_ACTION_DURATION) {
                private boolean torn;
                @Override protected void update(float percent) {
                    effect.animate(percent * (LIFT + STRAIN + BREAK));
                    // Crossing the marker also works when fast-forward skips the entire strain phase.
                    if (!torn && effect.time >= LIFT + STRAIN) {
                        torn = true;
                        if (onTear != null) onTear.call();
                    }
                }
                @Override protected void end() {
                    effect.remove();
                    subscriber.onCompleted();
                }
            }));
        });
    }

    private VitalityLossEffect(Vector2 position, float width, float height, float offsetX, Kind kind) {
        this.kind = kind;
        boolean sanity = kind == Kind.SANITY;
        left = new TextureRegion(getTexture(sanity ? SANITY_ICON_LEFT : HEALTH_ICON_LEFT));
        right = new TextureRegion(getTexture(sanity ? SANITY_ICON_RIGHT : HEALTH_ICON_RIGHT));
        pixel = new TextureRegion(getTexture(PURE_WHITE_BACKGROUND));
        halo = getTexture("dice_shadow.png");
        accent = Color.valueOf(sanity ? "79BFFF" : kind == Kind.MONSTER ? "FF8547" : "FF4C68");
        initialHeight = height;
        aspect = width / height;
        startX = position.x + width / 2f; startY = position.y + height / 2f;
        targetX = MathUtils.clamp(VIEWPORT_WIDTH / 2f + offsetX, 90f, VIEWPORT_WIDTH - 90f);
        targetY = VIEWPORT_HEIGHT * .54f;
        setName("vitality-loss-" + kind.name().toLowerCase(java.util.Locale.ROOT));
        setTouchable(Touchable.disabled);
        animate(0);
    }

    private void animate(float elapsed) {
        time = elapsed;
        float lift = MathUtils.clamp(time / LIFT, 0, 1);
        float travel = Interpolation.pow3Out.apply(lift);
        centerX = MathUtils.lerp(startX, targetX, travel);
        centerY = MathUtils.lerp(startY, targetY, travel) + MathUtils.sin(lift * MathUtils.PI) * 30f;
        float peak = kind == Kind.MONSTER ? 116f : 108f;
        height = MathUtils.lerp(initialHeight, peak, Interpolation.swingOut.apply(lift));
        rotation = MathUtils.sin(lift * MathUtils.PI) * (kind == Kind.SANITY ? -16f : 12f);
        if (time > LIFT && time < LIFT + STRAIN) {
            float strain = (time - LIFT) / STRAIN;
            height *= 1f + .13f * strain;
            centerX += MathUtils.sin(strain * MathUtils.PI * 6f) * 2.5f * strain;
            rotation = MathUtils.sin(strain * MathUtils.PI * 4f) * 3f * strain;
        } else if (time >= LIFT + STRAIN) {
            height = peak * 1.13f;
        }
    }

    @Override public void draw(Batch batch, float parentAlpha) {
        savedColor.set(batch.getColor());
        float alpha = parentAlpha * getColor().a;
        float flight = MathUtils.clamp((time - LIFT - STRAIN) / BREAK, 0, 1);
        float charge = MathUtils.clamp(time / (LIFT + STRAIN), 0, 1);
        float fade = 1f - Interpolation.pow2In.apply(flight);
        float radius = height * (.78f + flight * .7f);
        batch.setColor(accent.r, accent.g, accent.b, alpha * (.15f + charge * .30f) * fade);
        batch.draw(halo, centerX - radius, centerY - radius, radius * 2, radius * 2);

        // A small ripple stays at the depleted slot, making the source of each loss readable.
        float sourcePulse = MathUtils.clamp(time / .38f, 0, 1);
        ring(batch, startX, startY, initialHeight * (.5f + sourcePulse), 1.5f,
                alpha * (1f - sourcePulse) * .8f, accent);

        if (flight == 0) {
            drawHalf(batch, left, centerX, centerY, height, rotation, alpha);
            drawHalf(batch, right, centerX, centerY, height, rotation, alpha);
            if (time > LIFT) {
                float tension = (time - LIFT) / STRAIN;
                crack(batch, tension, alpha);
            }
        } else {
            float spread = Interpolation.pow2Out.apply(flight);
            boolean sanity = kind == Kind.SANITY;
            float drop = sanity ? 26f * flight : 25f * flight - 120f * flight * flight;
            float size = height * (1f - .24f * flight);
            drawHalf(batch, left, centerX - 55f * spread, centerY + drop + (sanity ? 28f * flight : 0),
                    size, (sanity ? 42f : 60f) * flight, alpha * fade);
            drawHalf(batch, right, centerX + 55f * spread, centerY + drop - 18f * flight,
                    size, -74f * flight, alpha * fade);
            ring(batch, centerX, centerY, 18f + 100f * spread, 2.5f * (1 - flight) + .5f,
                    alpha * (1f - flight) * .8f, accent);
            particles(batch, flight, alpha);
            float flash = Math.max(0, 1f - flight / .18f);
            batch.setColor(1f, .9f, 1f, alpha * flash * .85f);
            batch.draw(halo, centerX - 48, centerY - 48, 96, 96);
        }
        batch.setColor(savedColor);
    }

    private void drawHalf(Batch batch, TextureRegion texture, float x, float y, float size, float angle, float alpha) {
        float width = size * aspect;
        batch.setColor(.04f, .02f, .08f, alpha * .55f);
        batch.draw(texture, x - width / 2 + 3, y - size / 2 - 5, width / 2, size / 2, width, size, 1, 1, angle);
        batch.setColor(1, 1, 1, alpha);
        batch.draw(texture, x - width / 2, y - size / 2, width / 2, size / 2, width, size, 1, 1, angle);
    }

    private void crack(Batch batch, float tension, float alpha) {
        float previousX = centerX, previousY = centerY - height * .35f;
        for (int i = 1; i <= 5; i++) {
            float x = centerX + (i % 2 == 0 ? -1 : 1) * height * .045f;
            float y = centerY - height * .35f + height * .14f * i;
            line(batch, previousX, previousY, x, y, 5f, accent, alpha * tension * .35f);
            line(batch, previousX, previousY, x, y, 1.3f, Color.WHITE, alpha * tension * .9f);
            previousX = x; previousY = y;
        }
    }

    private void particles(Batch batch, float flight, float alpha) {
        boolean sanity = kind == Kind.SANITY;
        for (int i = 0; i < 18; i++) {
            float angle = i * 137.508f + (sanity ? 38f * flight : 0);
            float speed = 50f + (i % 5) * 19f;
            float distance = speed * Interpolation.pow2Out.apply(flight);
            float x = centerX + MathUtils.cosDeg(angle) * distance;
            float y = centerY + MathUtils.sinDeg(angle) * distance * .8f
                    + (sanity ? 48f : -100f) * flight * flight;
            float opacity = alpha * (1f - flight) * (i % 3 == 0 ? .65f : 1f);
            float length = (sanity ? 6f : 10f) * (1f - flight) + 2f;
            Color color = i % 3 == 0 ? Color.WHITE : accent;
            if (sanity) {
                batch.setColor(.55f, .35f, 1f, opacity * .45f);
                batch.draw(halo, x - 10, y - 10, 20, 20);
            }
            line(batch, x, y, x + MathUtils.cosDeg(angle) * length,
                    y + MathUtils.sinDeg(angle) * length, 1.5f + (i % 3), color, opacity);
        }
    }

    private void ring(Batch batch, float x, float y, float radius, float width, float alpha, Color color) {
        if (alpha <= 0) return;
        for (int i = 0; i < 40; i++) {
            float a = i * 9f, b = a + 9f;
            line(batch, x + MathUtils.cosDeg(a) * radius, y + MathUtils.sinDeg(a) * radius,
                    x + MathUtils.cosDeg(b) * radius, y + MathUtils.sinDeg(b) * radius, width, color, alpha);
        }
    }

    private void line(Batch batch, float x, float y, float endX, float endY, float width, Color color, float alpha) {
        float dx = endX - x, dy = endY - y;
        batch.setColor(color.r, color.g, color.b, alpha);
        batch.draw(pixel, x, y - width / 2, 0, width / 2, (float) Math.sqrt(dx * dx + dy * dy), width,
                1, 1, MathUtils.atan2(dy, dx) * MathUtils.radiansToDegrees);
    }

    @Override protected void setStage(Stage stage) {
        super.setStage(stage);
        if (stage == null) clearActions();
    }
}
