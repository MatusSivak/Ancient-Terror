package sk.sivak.eldritchhorror.core.view.components.combat;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.utils.FloatArray;
import sk.sivak.eldritchhorror.core.view.utils.FastForwardAction;

import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.GATE_SMOKE;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.getTexture;

/** Flames and smoke shed along a fireball's path. Outlives the fireball until the last puff has faded. */
final class FireballTrail extends Actor {

    private static final float FIRE_LIFE = .26f;
    private static final float SMOKE_LIFE = .8f;
    private static final int JITTERS = 16;
    private static final float[] JITTER_X = new float[JITTERS], JITTER_Y = new float[JITTERS];

    static {
        java.util.Random random = new java.util.Random(7);
        for (int i = 0; i < JITTERS; i++) {
            JITTER_X[i] = random.nextFloat() * 2f - 1f;
            JITTER_Y[i] = random.nextFloat() * 2f - 1f;
        }
    }

    private final FireballPalette palette;
    private final TextureRegion[] frames = FireballPalette.frames();
    private final Texture smoke = getTexture(GATE_SMOKE);
    /** x, y, size and emission time of every sample. */
    private final FloatArray samples = new FloatArray();
    private final Color tmp = new Color();
    private int first;
    private float clock;
    private boolean ended;

    FireballTrail(boolean blue) {
        palette = FireballPalette.of(blue);
        setName("combat-fireball-trail");
        setTouchable(Touchable.disabled);
    }

    void emit(float x, float y, float size) {
        if (!ended && size > 1f) samples.addAll(x, y, size, clock);
    }

    /** No more samples; the trail removes itself once the remaining ones fade. */
    void end() {
        ended = true;
    }

    @Override public void act(float delta) {
        super.act(delta);
        clock += delta * FastForwardAction.getSpeed();
        while (first < samples.size && clock - samples.get(first + 3) >= SMOKE_LIFE) first += 4;
        if (ended && first >= samples.size) remove();
    }

    @Override public void draw(Batch batch, float parentAlpha) {
        if (first >= samples.size) return;
        float previous = batch.getPackedColor();
        int src = batch.getBlendSrcFunc(), dst = batch.getBlendDstFunc();

        batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        for (int i = first; i < samples.size; i += 4) {
            float age = clock - samples.get(i + 3);
            float k = age / SMOKE_LIFE;
            if (k >= 1f) continue;
            int n = i / 4;
            float size = samples.get(i + 2);
            float drift = size * .2f * Interpolation.pow2Out.apply(k);
            float x = samples.get(i) + JITTER_X[n % JITTERS] * drift;
            float y = samples.get(i + 1) + JITTER_Y[n % JITTERS] * drift + size * .12f * k;
            float puff = size * (.3f + .7f * Interpolation.pow2Out.apply(k));
            float a = .5f * (float) Math.pow(1f - k, 1.6f) * Math.min(1f, age / .08f);
            tmp.set(palette.scorched).lerp(palette.smoke, Math.min(1f, k * 4f));
            batch.setColor(tmp.r, tmp.g, tmp.b, a * parentAlpha);
            FireballPalette.draw(batch, smoke, x, y, puff, puff, n * 37f + k * 50f);
        }

        batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE);
        ShaderProgram shader = batch.getShader();
        palette.beginFlames(batch);
        for (int i = first; i < samples.size; i += 4) {
            float age = clock - samples.get(i + 3);
            float k = age / FIRE_LIFE;
            if (k >= 1f) continue;
            int n = i / 4;
            float size = samples.get(i + 2) * (.65f * (1f - k) + .12f);
            tmp.set(palette.hot).lerp(palette.ember, Math.min(1f, k * 1.3f));
            batch.setColor(tmp.r, tmp.g, tmp.b, .6f * (1f - k) * parentAlpha);
            FireballPalette.draw(batch, frames[(n * 5 + (int) (age * 28f)) % frames.length],
                    samples.get(i), samples.get(i + 1), size, size, n * 47f);
        }
        batch.setShader(shader);

        batch.setBlendFunction(src, dst);
        batch.setColor(previous);
    }
}
