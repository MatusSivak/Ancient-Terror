package sk.sivak.eldritchhorror.core.view.map.storm;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import sk.sivak.eldritchhorror.core.view.animation.AnimatedImage;
import sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager;

/** Wisps gather around an opening eye; resolving it pulls the entire storm back into that eye. */
public class StormImage extends AnimatedImage {
    private boolean spawning, vanishing;
    private float spawnProgress, vanishProgress, idleTime;
    private float vanishRotation, vanishScale, vanishAlpha;

    public StormImage() {
        super(new Animation<>(0.05f, CustomAssetManager.getStormAnimation(), Animation.PlayMode.LOOP));
        setOrigin(200, 200);
    }

    public void setSpawnProgress(float progress) {
        spawning = true;
        vanishing = false;
        spawnProgress = MathUtils.clamp(progress, 0f, 1f);
        float reveal = Interpolation.pow3Out.apply(MathUtils.clamp((spawnProgress - .15f) / .85f, 0f, 1f));
        setScale(.02f + .98f * reveal + .12f * MathUtils.sin(spawnProgress * MathUtils.PI));
        setRotation(-320f * (1f - reveal));
        setColor(.35f + .65f * reveal, .7f + .3f * reveal, 1f, Math.min(1f, reveal * 2f));
    }

    public void finishSpawn() {
        spawning = false;
        vanishing = false;
        idleTime = 0f;
        setScale(1f);
        setRotation(0f);
        setColor(1f, 1f, 1f, 1f);
    }

    public void beginVanish() {
        spawning = false;
        vanishing = true;
        vanishProgress = 0f;
        vanishRotation = getRotation();
        vanishScale = getScaleX();
        vanishAlpha = getColor().a;
    }

    public void setVanishProgress(float progress) {
        vanishProgress = MathUtils.clamp(progress, 0f, 1f);
        float collapse = Interpolation.pow2In.apply(vanishProgress);
        setScale(vanishScale * (1f - collapse));
        setRotation(vanishRotation + 300f * collapse);
        setColor(1f - .55f * collapse, 1f - .2f * collapse, 1f, vanishAlpha * (1f - collapse));
    }

    @Override public void act(float delta) {
        super.act(delta);
        if (!spawning && !vanishing && getActions().size == 0) {
            idleTime += delta;
            setRotation(idleTime * 30f);
            float pulse = .5f - .5f * MathUtils.cos(idleTime * MathUtils.PI2 / 1.5f);
            setScale(1f + .1f * pulse);
            setColor(.7f + .3f * pulse, .85f + .15f * pulse, 1f, getColor().a);
        }
    }

    @Override public void draw(Batch batch, float parentAlpha) {
        drawVortex(batch, parentAlpha);
        super.draw(batch, parentAlpha);
    }

    private void drawVortex(Batch batch, float parentAlpha) {
        float p = vanishing ? vanishProgress : spawnProgress;
        boolean transition = spawning || vanishing;
        float energy = transition ? MathUtils.sin(p * MathUtils.PI) : .10f;
        if (energy <= 0f) return;
        float saved = batch.getPackedColor();
        int src = batch.getBlendSrcFunc(), dst = batch.getBlendDstFunc();
        int srcAlpha = batch.getBlendSrcFuncAlpha(), dstAlpha = batch.getBlendDstFuncAlpha();
        Texture smoke = CustomAssetManager.getTexture(CustomAssetManager.GATE_SMOKE);
        Texture white = CustomAssetManager.getTexture(CustomAssetManager.PURE_WHITE_BACKGROUND);
        float cx = getX() + getOriginX(), cy = getY() + getOriginY();
        float turn = transition ? p * (vanishing ? 420f : 240f) : idleTime * 18f;
        float radius = getWidth() * (transition ? (vanishing ? .48f * (1f - p) : .72f - .38f * p) : .42f);
        batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE);
        for (int i = 0; i < 24; i++) {
            float angle = i * 137.5f + turn;
            float distance = radius * (.55f + .45f * (i % 6) / 5f);
            float size = getWidth() * (.11f + .08f * (i % 3));
            batch.setColor(.20f, .60f, .85f, parentAlpha * energy * .32f);
            batch.draw(smoke, cx + MathUtils.cosDeg(angle) * distance - size / 2,
                    cy + MathUtils.sinDeg(angle) * distance * .72f - size / 2, size, size);
        }
        // Broken concentric arcs make the movement readable over both land and ocean.
        for (int ring = 0; ring < 2; ring++) {
            float r = radius * (ring == 0 ? 1f : .7f);
            for (int i = 0; i < 36; i++) {
                if (i % 9 > 5) continue;
                float angle = i * 10f + turn * (ring == 0 ? 1f : -.8f);
                float x = cx + MathUtils.cosDeg(angle) * r, y = cy + MathUtils.sinDeg(angle) * r * .72f;
                float dx = cx + MathUtils.cosDeg(angle + 7f) * r - x;
                float dy = cy + MathUtils.sinDeg(angle + 7f) * r * .72f - y;
                batch.setColor(.40f, .85f, 1f, parentAlpha * energy * .65f);
                batch.draw(white, x, y, 0, 0, (float)Math.sqrt(dx * dx + dy * dy), 1.5f,
                        1f, 1f, MathUtils.atan2(dy, dx) * MathUtils.radiansToDegrees,
                        0, 0, white.getWidth(), white.getHeight(), false, false);
            }
        }
        batch.setBlendFunctionSeparate(src, dst, srcAlpha, dstAlpha);
        batch.setColor(saved);
    }
}
