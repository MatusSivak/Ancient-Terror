package sk.sivak.eldritchhorror.core.view.components.combat;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction;
import sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager;
import sk.sivak.eldritchhorror.core.view.utils.FastForwardAction;

/** A fireball blown apart by a bullet: a hot flash, a shock ring, embers flung along the shot and a puff of smoke. */
final class FireballShatter extends Actor {
    private static final float DURATION = 0.6f;
    private static final int EMBERS = 26;

    private final Texture glow = CustomAssetManager.getTexture(CustomAssetManager.LIGHT);
    private final Texture smoke = CustomAssetManager.getTexture(CustomAssetManager.GATE_SMOKE);
    private final Color tint;
    private final float scale;
    private final float[] angles = new float[EMBERS];
    private final float[] speeds = new float[EMBERS];
    private final float[] sizes = new float[EMBERS];
    private float progress;

    private FireballShatter(Vector2 center, Vector2 shotDirection, Color tint, float scale) {
        this.tint = new Color(tint);
        this.scale = scale;
        setPosition(center.x, center.y);
        setName("combat-fireball-shatter");
        setTouchable(Touchable.disabled);
        float shotAngle = shotDirection.isZero() ? 90f : shotDirection.angle();
        for (int i = 0; i < EMBERS; i++) {
            // Most pieces are knocked onward by the bullet, the rest scatter all around.
            angles[i] = i % 3 == 0 ? MathUtils.random(360f) : shotAngle + MathUtils.random(-50f, 50f);
            speeds[i] = MathUtils.random(60f, 190f) * scale;
            sizes[i] = MathUtils.random(7f, 16f) * scale;
        }
    }

    static void burst(Group layer, Vector2 center, Vector2 shotDirection, Color tint, float scale) {
        FireballShatter shatter = new FireballShatter(center, shotDirection, tint, scale);
        layer.addActor(shatter);
        shatter.addAction(new FastForwardAction<>(Actions.sequence(new TemporalAction(DURATION) {
            @Override protected void update(float percent) { shatter.progress = percent; }
        }, Actions.removeActor())));
    }

    @Override public void draw(Batch batch, float parentAlpha) {
        float saved = batch.getPackedColor();
        float p = progress;
        float travel = Interpolation.pow3Out.apply(p);

        float puff = (40f + 70f * travel) * scale;
        batch.setColor(0.25f, 0.22f, 0.2f, parentAlpha * 0.5f * (1f - p));
        batch.draw(smoke, getX() - puff / 2f, getY() - puff / 2f + 18f * travel, puff / 2f, puff / 2f, puff, puff,
                1f, 1f, 60f * p, 0, 0, smoke.getWidth(), smoke.getHeight(), false, false);

        int src = batch.getBlendSrcFunc();
        int dst = batch.getBlendDstFunc();
        batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE);

        float flashLife = Math.min(1f, p / 0.35f);
        float flash = (60f + 90f * Interpolation.pow2Out.apply(flashLife)) * scale;
        batch.setColor(1f, 1f, 0.9f, parentAlpha * (1f - flashLife));
        batch.draw(glow, getX() - flash / 2f, getY() - flash / 2f, flash, flash);
        batch.setColor(tint.r, tint.g, tint.b, parentAlpha * 0.9f * (1f - flashLife));
        float core = flash * 1.5f;
        batch.draw(glow, getX() - core / 2f, getY() - core / 2f, core, core);

        float ringLife = Math.min(1f, p / 0.45f);
        float ring = (30f + 150f * Interpolation.pow2Out.apply(ringLife)) * scale;
        batch.setColor(tint.r, tint.g, tint.b, parentAlpha * 0.55f * (1f - ringLife));
        batch.draw(glow, getX() - ring / 2f, getY() - ring * 0.35f / 2f, ring, ring * 0.35f);
        batch.draw(glow, getX() - ring * 0.35f / 2f, getY() - ring / 2f, ring * 0.35f, ring);

        float emberAlpha = parentAlpha * (1f - Interpolation.pow2In.apply(p));
        for (int i = 0; i < EMBERS; i++) {
            float distance = speeds[i] * travel;
            float x = getX() + MathUtils.cosDeg(angles[i]) * distance;
            float y = getY() + MathUtils.sinDeg(angles[i]) * distance - 70f * scale * p * p;
            float size = sizes[i] * (1f - 0.6f * p);
            float length = size * (1f + 2.2f * (1f - p));
            // Embers cool from white-hot to the fireball's colour.
            batch.setColor(MathUtils.lerp(1f, tint.r, p), MathUtils.lerp(0.95f, tint.g, p),
                    MathUtils.lerp(0.8f, tint.b, p), emberAlpha);
            batch.draw(glow, x - length / 2f, y - size / 2f, length / 2f, size / 2f, length, size, 1f, 1f,
                    angles[i], 0, 0, glow.getWidth(), glow.getHeight(), false, false);
        }

        batch.setBlendFunction(src, dst);
        batch.setColor(saved);
    }
}
