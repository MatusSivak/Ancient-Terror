package sk.sivak.eldritchhorror.core.view.map.storm;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import sk.sivak.eldritchhorror.core.view.animation.AnimatedImage;
import sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager;

/** The rumor coalesces in place, then keeps a slow, frame-rate-independent swirl. */
public class StormImage extends AnimatedImage {
    private boolean spawning;
    private float spawnProgress, idleTime;

    public StormImage() {
        super(new Animation<>(0.05f, CustomAssetManager.getStormAnimation(), Animation.PlayMode.LOOP));
        setOrigin(200, 200);
    }

    public void setSpawnProgress(float progress) {
        spawning = true;
        spawnProgress = progress;
        float reveal = Interpolation.pow3Out.apply(progress);
        setScale(.04f + .96f * reveal + .14f * MathUtils.sin(progress * MathUtils.PI));
        setRotation(-240f * (1f - reveal));
        setColor(.45f + .55f * reveal, .75f + .25f * reveal, 1f, Math.min(1f, progress * 4f));
    }

    public void finishSpawn() {
        spawning = false;
        idleTime = 0f;
        setScale(1f);
        setRotation(0f);
        setColor(1f, 1f, 1f, 1f);
    }

    @Override public void act(float delta) {
        super.act(delta);
        if (!spawning && getActions().size == 0) {
            idleTime += delta;
            setRotation(idleTime * 30f);
            float pulse = .5f - .5f * MathUtils.cos(idleTime * MathUtils.PI2 / 1.5f);
            setScale(1f + .1f * pulse);
            setColor(.7f + .3f * pulse, .85f + .15f * pulse, 1f, getColor().a);
        }
    }

    @Override public void draw(Batch batch, float parentAlpha) {
        if (spawning && spawnProgress > 0f) {
            float saved = batch.getPackedColor();
            float p = spawnProgress;
            float radius = getWidth() * (.08f + .65f * Interpolation.pow2Out.apply(p));
            batch.setColor(.15f, .65f, 1f, parentAlpha * (1f - p) * Math.min(1f, p * 8f) * .6f);
            for (int i = 0; i < 12; i++) {
                float angle = i * 30f - p * 150f;
                float size = getWidth() * (.12f + .18f * p);
                batch.draw(CustomAssetManager.getTexture(CustomAssetManager.GATE_SMOKE),
                        getX() + getOriginX() + MathUtils.cosDeg(angle) * radius - size / 2,
                        getY() + getOriginY() + MathUtils.sinDeg(angle) * radius * .6f - size / 2, size, size);
            }
            batch.setColor(saved);
        }
        super.draw(batch, parentAlpha);
    }
}
