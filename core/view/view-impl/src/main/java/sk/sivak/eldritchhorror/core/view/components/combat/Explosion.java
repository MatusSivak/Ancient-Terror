package sk.sivak.eldritchhorror.core.view.components.combat;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.utils.Array;
import sk.sivak.eldritchhorror.core.view.utils.EffectTextures;
import sk.sivak.eldritchhorror.core.view.utils.FastForwardAction;

import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.GATE_SMOKE;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.getTexture;

/**
 * Fireball hits on one target: a short flash, a small shock ring, a burst of flame, sparks and a puff
 * of smoke. Several fireballs can hit the same target at once.
 */
public class Explosion extends Actor {

    private static final float LIFE = 1f;
    private static final int SPARKS = 12, PUFFS = 7, BURSTS = 4;

    private final Array<Impact> impacts = new Array<>();
    private final TextureRegion[] frames = FireballPalette.frames();
    private final Texture smoke = getTexture(GATE_SMOKE);
    private final Color tmp = new Color();
    private Group home;
    private float clock;
    private boolean started;

    public Explosion() {
        setName("combat-fireball-impact");
        setTouchable(Touchable.disabled);
    }

    /** Group the explosion re-joins when it starts; it leaves the stage once its effects are done. */
    void setHome(Group home) {
        this.home = home;
    }

    public void start(ExplosionConfig config) {
        if (getParent() == null && home != null) {
            home.addActor(this);
        }
        started = true;
        impacts.add(new Impact(config, clock));
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        clock += delta * FastForwardAction.getSpeed();
        for (int i = impacts.size - 1; i >= 0; i--) {
            if (clock - impacts.get(i).start >= LIFE) impacts.removeIndex(i);
        }
        if (started && impacts.size == 0) {
            remove();
        }
    }

    /** Drops every running impact and leaves the stage. */
    public void dispose() {
        impacts.clear();
        remove();
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (impacts.size == 0) return;
        float previous = batch.getPackedColor();
        int src = batch.getBlendSrcFunc(), dst = batch.getBlendDstFunc();

        batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        for (Impact impact : impacts) drawSmoke(batch, impact, clock - impact.start, parentAlpha);
        batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE);
        for (Impact impact : impacts) drawFlash(batch, impact, clock - impact.start, parentAlpha);
        ShaderProgram shader = batch.getShader();
        for (Impact impact : impacts) {
            impact.palette.beginFlames(batch);
            drawBurst(batch, impact, clock - impact.start, parentAlpha);
            batch.setShader(shader);
        }

        batch.setBlendFunction(src, dst);
        batch.setColor(previous);
    }

    private void drawSmoke(Batch batch, Impact impact, float it, float alpha) {
        FireballPalette palette = impact.palette;
        for (int i = 0; i < PUFFS; i++) {
            float k = it / impact.puffLife[i];
            if (k >= 1f) continue;
            float distance = impact.puffDistance[i] * Interpolation.pow3Out.apply(k);
            float x = impact.x + MathUtils.cosDeg(impact.puffAngle[i]) * distance;
            float y = impact.y + MathUtils.sinDeg(impact.puffAngle[i]) * distance * .8f + 22f * impact.scale * k;
            float size = impact.puffSize[i] * (.45f + .85f * Interpolation.pow2Out.apply(k));
            float a = .55f * (float) Math.pow(1f - k, 1.6f) * Math.min(1f, it / .05f);
            tmp.set(palette.scorched).lerp(palette.smoke, Math.min(1f, k * 3.5f));
            batch.setColor(tmp.r, tmp.g, tmp.b, a * alpha);
            FireballPalette.draw(batch, smoke, x, y, size, size, impact.puffSpin[i] + k * 60f);
        }
    }

    private void drawFlash(Batch batch, Impact impact, float it, float alpha) {
        float s = impact.scale;
        float flash = it / .3f;
        if (flash < 1f) {
            float radius = (30f + 65f * Interpolation.pow2Out.apply(Math.min(1f, it / .15f))) * s;
            float a = (1f - flash) * (1f - flash) * impact.strobe * alpha;
            tmp.set(impact.tint).lerp(Color.WHITE, .4f);
            batch.setColor(tmp.r, tmp.g, tmp.b, .8f * a);
            FireballPalette.draw(batch, EffectTextures.glow(), impact.x, impact.y, radius * 2f, radius * 2f, 0f);
            batch.setColor(1f, 1f, 1f, a);
            FireballPalette.draw(batch, EffectTextures.glow(), impact.x, impact.y, radius * .6f, radius * .6f, 0f);
        }
        float wave = it / .4f;
        if (wave < 1f) {
            float radius = (18f + 95f * Interpolation.pow3Out.apply(wave)) * s;
            tmp.set(Color.WHITE).lerp(impact.tint, .55f);
            batch.setColor(tmp.r, tmp.g, tmp.b, .6f * (float) Math.pow(1f - wave, 1.5f) * impact.strobe * alpha);
            FireballPalette.draw(batch, EffectTextures.ring(), impact.x, impact.y, radius * 2f, radius * 2f, 0f);
        }
        for (int i = 0; i < SPARKS; i++) {
            float k = it / impact.sparkLife[i];
            if (k >= 1f) continue;
            float distance = impact.sparkDistance[i] * Interpolation.pow2Out.apply(k);
            float x = impact.x + MathUtils.cosDeg(impact.sparkAngle[i]) * distance;
            float y = impact.y + MathUtils.sinDeg(impact.sparkAngle[i]) * distance - 30f * s * k * k;
            tmp.set(impact.palette.hot).lerp(impact.palette.fire, k);
            batch.setColor(tmp.r, tmp.g, tmp.b, (1f - k) * alpha);
            FireballPalette.draw(batch, EffectTextures.glow(), x, y, (20f * (1f - k) + 6f) * s, 6f * s, impact.sparkAngle[i]);
        }
    }

    private void drawBurst(Batch batch, Impact impact, float it, float alpha) {
        float burst = it / .32f;
        if (burst >= 1f) return;
        float s = impact.scale;
        TextureRegion frame = frames[(int) (it * 28f) % frames.length];
        for (int i = 0; i < BURSTS; i++) {
            float distance = impact.burstDistance[i] * Interpolation.pow2Out.apply(burst);
            float size = (45f + 50f * burst) * s;
            tmp.set(impact.palette.hot).lerp(impact.palette.fire, burst);
            batch.setColor(tmp.r, tmp.g, tmp.b, .9f * (float) Math.pow(1f - burst, 1.5f) * alpha);
            FireballPalette.draw(batch, frame, impact.x + MathUtils.cosDeg(impact.burstAngle[i]) * distance,
                    impact.y + MathUtils.sinDeg(impact.burstAngle[i]) * distance, size, size, impact.burstAngle[i] + it * 120f);
        }
    }

    private static final class Impact {
        final float x, y, scale, start, strobe;
        final Color tint;
        final FireballPalette palette;
        final float[] sparkAngle = new float[SPARKS], sparkDistance = new float[SPARKS], sparkLife = new float[SPARKS];
        final float[] puffAngle = new float[PUFFS], puffDistance = new float[PUFFS], puffSize = new float[PUFFS],
                puffLife = new float[PUFFS], puffSpin = new float[PUFFS];
        final float[] burstAngle = new float[BURSTS], burstDistance = new float[BURSTS];

        Impact(ExplosionConfig config, float start) {
            x = config.position.x;
            y = config.position.y;
            scale = config.scale;
            tint = new Color(config.color);
            palette = FireballPalette.of(config.blue);
            this.start = start;
            boolean gentle = Gdx.app != null
                    && Gdx.app.getPreferences("AncientTerror.xml").getBoolean("strobe_disabled", false);
            strobe = gentle ? .45f : 1f;
            for (int i = 0; i < SPARKS; i++) {
                sparkAngle[i] = i * 360f / SPARKS + MathUtils.random(-12f, 12f);
                sparkDistance[i] = MathUtils.random(50f, 120f) * scale;
                sparkLife[i] = MathUtils.random(.25f, .45f);
            }
            for (int i = 0; i < PUFFS; i++) {
                puffAngle[i] = i * 360f / PUFFS + MathUtils.random(-18f, 18f);
                puffDistance[i] = MathUtils.random(22f, 50f) * scale;
                puffSize[i] = MathUtils.random(55f, 85f) * scale;
                puffLife[i] = MathUtils.random(.6f, .95f);
                puffSpin[i] = MathUtils.random(360f);
            }
            for (int i = 0; i < BURSTS; i++) {
                burstAngle[i] = i * 360f / BURSTS + MathUtils.random(30f);
                burstDistance[i] = MathUtils.random(10f, 26f) * scale;
            }
        }
    }

    public static class ExplosionConfig {
        private final Vector2 position;
        private final Color color;
        private final float scale;
        private final boolean blue;

        public ExplosionConfig(Vector2 position) {
            this(position, Color.WHITE);
        }

        public ExplosionConfig(Vector2 position, Color color) {
            this(position, color, 1f, false);
        }

        /** @param scale size of the hit relative to an ordinary fireball; {@code blue} for horror fire */
        public ExplosionConfig(Vector2 position, Color color, float scale, boolean blue) {
            this.position = new Vector2(position);
            this.color = new Color(color);
            this.scale = scale;
            this.blue = blue;
        }
    }
}
