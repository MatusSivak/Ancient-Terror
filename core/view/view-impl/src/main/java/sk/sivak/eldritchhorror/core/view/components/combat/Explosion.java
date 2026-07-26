package sk.sivak.eldritchhorror.core.view.components.combat;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;

public class Explosion extends Actor {

    private ExplosionConfig config;
    private final ParticleEffectPool effectPool;
    private final Array<ParticleEffectPool.PooledEffect> effects = new Array();

    public Explosion() {
        ParticleEffect particleEffect = new ParticleEffect();
        particleEffect.load(Gdx.files.internal("particle/explosion.p"), Gdx.files.internal("particle"));
        effectPool = new ParticleEffectPool(particleEffect, 5, 10);
    }

    public void start(ExplosionConfig config) {
        this.config = config;
        ParticleEffectPool.PooledEffect pooledEffect = effectPool.obtain();
        pooledEffect.start();
        pooledEffect.setPosition(config.position.x, config.position.y);
        pooledEffect.getEmitters().get(0).getTint().setColors(
                new float[]{config.color.r, config.color.g, config.color.b, config.color.a});
        effects.add(pooledEffect);
    }

    public void act(float delta) {
        for (int i = effects.size - 1; i >= 0; i--) {
            ParticleEffectPool.PooledEffect pooledEffect = effects.get(i);
            pooledEffect.update(delta);
            if (pooledEffect.isComplete()) {
                pooledEffect.free();
                effects.removeIndex(i);
            }
        }
    }

    public static class ExplosionConfig {
        private Vector2 position;
        private Color color;

        public ExplosionConfig(Vector2 position) {
            this(position, Color.WHITE);
        }

        public ExplosionConfig(Vector2 position, Color color) {
            this.position = position;
            this.color = color;
        }
    }


    @Override
    public void draw(Batch batch, float parentAlpha) {
        for (ParticleEffectPool.PooledEffect effect : effects) {
            effect.draw(batch);
        }
    }
}
