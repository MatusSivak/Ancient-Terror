package sk.sivak.eldritchhorror.core.view.map.helper;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
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
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import sk.sivak.eldritchhorror.core.view.game.MapStage;
import sk.sivak.eldritchhorror.core.view.map.MapLightSource;
import sk.sivak.eldritchhorror.core.view.utils.EffectTextures;
import sk.sivak.eldritchhorror.core.view.utils.FastForwardAction;

import java.util.Random;

import static sk.sivak.eldritchhorror.core.constants.ViewProperties.MAP_WIDTH;
import static sk.sivak.eldritchhorror.core.constants.ViewProperties.NORMAL_ACTION_DURATION;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.GATE_SMOKE;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.getFireballFrames;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.getTexture;

/**
 * A burning comet that streaks down onto a map location, followed by a flash, a shockwave, sparks and
 * billowing smoke. Lives in the sky layer above tokens; all world copies share the same seeded shape.
 */
public final class MapImpactComet extends Actor implements MapLightSource {

    /** Times are in units of {@code NORMAL_ACTION_DURATION}. */
    private static final float FLIGHT = .72f;
    private static final float AFTERMATH = 1.6f;
    private static final float TRAIL_STEP = 1f / 60f;
    private static final float FIRE_LIFE = .36f;
    private static final float SMOKE_LIFE = 1.1f;
    private static final float HEAD_SIZE = 118f;
    private static final Color FIRE = new Color(1f, .55f, .2f, 1f);
    private static final Color HOT = new Color(1f, .92f, .7f, 1f);
    private static final Color SMOKE = new Color(.24f, .21f, .2f, 1f);
    private static final Color SCORCHED_SMOKE = new Color(.9f, .5f, .25f, 1f);
    private static final int SPARKS = 22, PUFFS = 14, BURSTS = 7, JITTERS = 16;

    private final TextureRegion[] frames;
    private final Texture smoke = getTexture(GATE_SMOKE);
    private final float tx, ty, sx, sy, travelAngle, scale;
    private final Color glowColor;
    private final Color tmp = new Color();
    private final Vector2 point = new Vector2();
    private final boolean gentle;
    private final float[] sparkAngle = new float[SPARKS], sparkDistance = new float[SPARKS], sparkLife = new float[SPARKS];
    private final float[] puffAngle = new float[PUFFS], puffDistance = new float[PUFFS], puffSize = new float[PUFFS],
            puffLife = new float[PUFFS], puffSpin = new float[PUFFS];
    private final float[] burstAngle = new float[BURSTS], burstDistance = new float[BURSTS];
    private final float[] jitterX = new float[JITTERS], jitterY = new float[JITTERS];
    private float clock;

    public MapImpactComet(float targetX, float targetY, Color gateTint) {
        tx = targetX;
        ty = targetY;
        scale = Math.max(.001f, NORMAL_ACTION_DURATION);
        glowColor = new Color(FIRE).lerp(gateTint, .45f);
        gentle = Gdx.app != null && Gdx.app.getPreferences("AncientTerror.xml").getBoolean("strobe_disabled", false);
        TextureRegionDrawable[] drawables = getFireballFrames();
        frames = new TextureRegion[drawables.length];
        for (int i = 0; i < drawables.length; i++) frames[i] = drawables[i].getRegion();

        // The wrapped copies share the location modulo the map width, so they fly the same path.
        int wrapped = (int) (((targetX % MAP_WIDTH) + MAP_WIDTH) % MAP_WIDTH);
        Random random = new Random(wrapped * 31L + (int) targetY);
        float side = random.nextBoolean() ? 1f : -1f;
        OrthographicCamera camera = MapStage.getCamera();
        float visibleTop = camera.position.y + camera.viewportHeight * camera.zoom / 2f;
        // Enter just above the visible area so the whole descent is on screen.
        float drop = Math.max(260f, visibleTop - targetY + HEAD_SIZE * .5f);
        sx = targetX - side * drop * .6f;
        sy = targetY + drop;
        travelAngle = MathUtils.atan2(ty - sy, tx - sx) * MathUtils.radiansToDegrees;

        for (int i = 0; i < SPARKS; i++) {
            sparkAngle[i] = i * 360f / SPARKS + (random.nextFloat() - .5f) * 14f;
            sparkDistance[i] = 140f + random.nextFloat() * 230f;
            sparkLife[i] = .35f + random.nextFloat() * .35f;
        }
        for (int i = 0; i < PUFFS; i++) {
            puffAngle[i] = i * 360f / PUFFS + (random.nextFloat() - .5f) * 20f;
            puffDistance[i] = 95f + random.nextFloat() * 100f;
            puffSize[i] = 80f + random.nextFloat() * 60f;
            puffLife[i] = .9f + random.nextFloat() * .45f;
            puffSpin[i] = random.nextFloat() * 360f;
        }
        for (int i = 0; i < BURSTS; i++) {
            burstAngle[i] = i * 360f / BURSTS + random.nextFloat() * 30f;
            burstDistance[i] = 40f + random.nextFloat() * 50f;
        }
        for (int i = 0; i < JITTERS; i++) {
            jitterX[i] = random.nextFloat() * 2f - 1f;
            jitterY[i] = random.nextFloat() * 2f - 1f;
        }

        setName("map-impact-comet");
        setTouchable(Touchable.disabled);
        addAction(new FastForwardAction<>(new TemporalAction((FLIGHT + AFTERMATH) * scale) {
            @Override protected void update(float percent) { clock = percent * getDuration(); }
            @Override protected void end() { remove(); }
        }));
    }

    /** Real seconds from launch until the comet hits the ground. */
    public static float flightTime() {
        return FLIGHT * NORMAL_ACTION_DURATION;
    }

    @Override protected void setStage(Stage stage) {
        super.setStage(stage);
        if (stage == null) clearActions();
    }

    private float time() {
        return clock / scale;
    }

    public boolean hasImpacted() {
        return time() >= FLIGHT;
    }

    /** Head position at normalized time {@code t}; the comet accelerates as it falls. */
    private Vector2 head(float t, Vector2 out) {
        float f = MathUtils.clamp(t / FLIGHT, 0f, 1f);
        float e = f * (.7f + .3f * f);
        return out.set(sx + (tx - sx) * e, sy + (ty - sy) * e);
    }

    @Override public float getMapLightStrength() {
        float t = time();
        float alpha = getColor().a;
        if (t < FLIGHT) return alpha * (.15f + .35f * t / FLIGHT);
        float k = (t - FLIGHT) / .6f;
        if (k >= 1f) return 0f;
        return alpha * (gentle ? .5f : 1f) * (1f - k) * (1f - k);
    }

    @Override public float getMapLightRadius() {
        float t = time();
        if (t < FLIGHT) return 210f;
        return 220f + 140f * Interpolation.pow2Out.apply(Math.min(1f, (t - FLIGHT) / .2f));
    }

    @Override public Color getMapLightColor() {
        return glowColor;
    }

    @Override public Vector2 getMapLightCenter(Vector2 out) {
        return time() < FLIGHT ? head(time(), out) : out.set(tx, ty);
    }

    @Override public void draw(Batch batch, float parentAlpha) {
        float alpha = parentAlpha * getColor().a;
        if (alpha <= 0f) return;
        float previous = batch.getPackedColor();
        int src = batch.getBlendSrcFunc(), dst = batch.getBlendDstFunc();
        int srcAlpha = batch.getBlendSrcFuncAlpha(), dstAlpha = batch.getBlendDstFuncAlpha();
        float t = time();

        batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        drawTrailSmoke(batch, t, alpha);
        if (t >= FLIGHT) drawBillows(batch, t - FLIGHT, alpha);
        batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE);
        drawTrailFire(batch, t, alpha);
        if (t < FLIGHT) drawHead(batch, t, alpha);
        else drawImpact(batch, t - FLIGHT, alpha);

        batch.setBlendFunctionSeparate(src, dst, srcAlpha, dstAlpha);
        batch.setColor(previous);
    }

    private void drawTrailSmoke(Batch batch, float t, float alpha) {
        int emitted = (int) (Math.min(t, FLIGHT) / TRAIL_STEP);
        for (int i = 0; i <= emitted; i++) {
            float age = t - i * TRAIL_STEP;
            float k = age / SMOKE_LIFE;
            if (k >= 1f || age < 0f) continue;
            head(i * TRAIL_STEP, point);
            float drift = 34f * Interpolation.pow2Out.apply(k);
            float x = point.x + jitterX[i % JITTERS] * drift;
            float y = point.y + jitterY[i % JITTERS] * drift + 22f * k;
            float size = 40f + 120f * Interpolation.pow2Out.apply(k);
            float a = .62f * (float) Math.pow(1f - k, 1.5f) * Math.min(1f, age / .08f);
            tmp.set(SCORCHED_SMOKE).lerp(SMOKE, Math.min(1f, k * 4f));
            batch.setColor(tmp.r, tmp.g, tmp.b, a * alpha);
            draw(batch, smoke, x, y, size, size, i * 37f + k * 50f);
        }
    }

    /** Flames shed along the path shrink and cool toward the gate's color. */
    private void drawTrailFire(Batch batch, float t, float alpha) {
        int emitted = (int) (Math.min(t, FLIGHT) / TRAIL_STEP);
        for (int i = 0; i <= emitted; i++) {
            float age = t - i * TRAIL_STEP;
            float k = age / FIRE_LIFE;
            if (k >= 1f || age < 0f) continue;
            head(i * TRAIL_STEP, point);
            float size = 95f * (1f - k) + 22f;
            tmp.set(FIRE).lerp(glowColor, Math.min(1f, k * 1.4f));
            batch.setColor(tmp.r, tmp.g, tmp.b, .75f * (1f - k) * alpha);
            draw(batch, frames[(i * 5 + (int) (age * 28f)) % frames.length], point.x, point.y, size, size, i * 47f);
        }
    }

    private void drawHead(Batch batch, float t, float alpha) {
        head(t, point);
        float approach = t / FLIGHT;
        float size = HEAD_SIZE * (.85f + .25f * approach);
        float flicker = .9f + .1f * MathUtils.sin(t * 41f) * MathUtils.sin(t * 23f);
        float backX = -MathUtils.cosDeg(travelAngle), backY = -MathUtils.sinDeg(travelAngle);

        batch.setColor(glowColor.r, glowColor.g, glowColor.b, .5f * flicker * alpha);
        draw(batch, getGlow(), point.x, point.y, size * 2.2f, size * 2.2f, 0f);
        // A stretched glow behind the head reads as the comet's burning tail.
        float tail = size * 2.8f;
        batch.setColor(FIRE.r, FIRE.g, FIRE.b, .7f * alpha);
        draw(batch, getGlow(), point.x + backX * tail * .38f, point.y + backY * tail * .38f, tail, size * .62f, travelAngle);
        batch.setColor(HOT.r, HOT.g, HOT.b, .55f * alpha);
        draw(batch, getGlow(), point.x + backX * tail * .2f, point.y + backY * tail * .2f, tail * .6f, size * .3f, travelAngle);

        batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        TextureRegion frame = frames[(int) (clock * 28f) % frames.length];
        batch.setColor(1f, .82f, .7f, alpha);
        draw(batch, frame, point.x, point.y, size, size, -t * 260f);
        batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE);
        batch.setColor(1f, 1f, 1f, .9f * flicker * alpha);
        draw(batch, getGlow(), point.x, point.y, size * .45f, size * .45f, 0f);
    }

    private void drawBillows(Batch batch, float it, float alpha) {
        for (int i = 0; i < PUFFS; i++) {
            float k = it / puffLife[i];
            if (k >= 1f) continue;
            float distance = puffDistance[i] * Interpolation.pow3Out.apply(k);
            float x = tx + MathUtils.cosDeg(puffAngle[i]) * distance;
            float y = ty + MathUtils.sinDeg(puffAngle[i]) * distance * .8f + 28f * k;
            float size = puffSize[i] * (.5f + .95f * Interpolation.pow2Out.apply(k));
            float a = .7f * (float) Math.pow(1f - k, 1.6f) * Math.min(1f, it / .04f);
            tmp.set(SCORCHED_SMOKE).lerp(SMOKE, Math.min(1f, k * 3.5f));
            batch.setColor(tmp.r, tmp.g, tmp.b, a * alpha);
            draw(batch, smoke, x, y, size, size, puffSpin[i] + k * 60f);
        }
    }

    private void drawImpact(Batch batch, float it, float alpha) {
        float strobe = gentle ? .45f : 1f;
        float flash = it / .45f;
        if (flash < 1f) {
            float radius = 70f + 200f * Interpolation.pow2Out.apply(Math.min(1f, it / .25f));
            float a = (1f - flash) * (1f - flash) * strobe * alpha;
            tmp.set(glowColor).lerp(Color.WHITE, .5f);
            batch.setColor(tmp.r, tmp.g, tmp.b, a);
            draw(batch, getGlow(), tx, ty, radius * 2f, radius * 2f, 0f);
            batch.setColor(1f, 1f, 1f, a);
            draw(batch, getGlow(), tx, ty, radius * .7f, radius * .7f, 0f);
        }
        float wave = it / .6f;
        if (wave < 1f) {
            float radius = 40f + 340f * Interpolation.pow3Out.apply(wave);
            tmp.set(Color.WHITE).lerp(glowColor, .5f);
            batch.setColor(tmp.r, tmp.g, tmp.b, .85f * (float) Math.pow(1f - wave, 1.5f) * strobe * alpha);
            draw(batch, getRing(), tx, ty, radius * 2f, radius * 2f, 0f);
        }
        float burst = it / .45f;
        if (burst < 1f) {
            TextureRegion frame = frames[(int) (clock * 28f) % frames.length];
            for (int i = 0; i < BURSTS; i++) {
                float distance = burstDistance[i] * Interpolation.pow2Out.apply(burst);
                float size = 80f + 110f * burst;
                tmp.set(HOT).lerp(FIRE, burst);
                batch.setColor(tmp.r, tmp.g, tmp.b, (float) Math.pow(1f - burst, 1.5f) * alpha);
                draw(batch, frame, tx + MathUtils.cosDeg(burstAngle[i]) * distance,
                        ty + MathUtils.sinDeg(burstAngle[i]) * distance, size, size, burstAngle[i] + it * 90f);
            }
        }
        for (int i = 0; i < SPARKS; i++) {
            float k = it / sparkLife[i];
            if (k >= 1f) continue;
            float distance = sparkDistance[i] * Interpolation.pow2Out.apply(k);
            float x = tx + MathUtils.cosDeg(sparkAngle[i]) * distance;
            float y = ty + MathUtils.sinDeg(sparkAngle[i]) * distance;
            tmp.set(HOT).lerp(FIRE, k);
            batch.setColor(tmp.r, tmp.g, tmp.b, (1f - k) * alpha);
            draw(batch, getGlow(), x, y, 34f * (1f - k) + 8f, 9f, sparkAngle[i]);
        }
    }

    private static void draw(Batch batch, Texture texture, float cx, float cy, float w, float h, float rotation) {
        batch.draw(texture, cx - w / 2f, cy - h / 2f, w / 2f, h / 2f, w, h, 1f, 1f, rotation,
                0, 0, texture.getWidth(), texture.getHeight(), false, false);
    }

    private static void draw(Batch batch, TextureRegion region, float cx, float cy, float w, float h, float rotation) {
        batch.draw(region, cx - w / 2f, cy - h / 2f, w / 2f, h / 2f, w, h, 1f, 1f, rotation);
    }

    private Texture getGlow() {
        return EffectTextures.glow();
    }

    private Texture getRing() {
        return EffectTextures.ring();
    }
}
