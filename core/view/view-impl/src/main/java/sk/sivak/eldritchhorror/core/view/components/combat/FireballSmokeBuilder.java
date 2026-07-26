package sk.sivak.eldritchhorror.core.view.components.combat;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class FireballSmokeBuilder {

    private static FireballSmokeBuilder instance;

    private static FireballSmokeBuilder getInstance() {
        if (instance == null) {
            instance = new FireballSmokeBuilder();
        }
        return instance;
    }

    public static void nullifyInstance() {
        instance = null;
    }

    private final ParticleEffectPool effectPool;

    private FireballSmokeBuilder() {
        ParticleEffect particleEffect = new ParticleEffect();
        particleEffect.load(Gdx.files.internal("particle/smoke.p"), Gdx.files.internal("particle"));
        effectPool = new ParticleEffectPool(particleEffect, 5, 10);
    }

    public static FireballSmoke get() {
        return new FireballSmoke(getInstance().effectPool.obtain());
    }

    public static class FireballSmoke extends Actor {
        private ParticleEffectPool.PooledEffect pooledEffect;

        public FireballSmoke(ParticleEffectPool.PooledEffect pooledEffect) {
            this.pooledEffect = pooledEffect;
        }

        @Override
        public void act(float delta) {
            super.act(delta);
            if (pooledEffect.isComplete()) {
                remove();
            }
            pooledEffect.update(delta);
        }

        @Override
        public void draw(Batch batch, float parentAlpha) {
            super.draw(batch, parentAlpha);
            pooledEffect.draw(batch);
        }

        public ParticleEffectPool.PooledEffect getParticleEffect() {
            return pooledEffect;
        }
    }
}
