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
        private boolean freed;

        public FireballSmoke(ParticleEffectPool.PooledEffect pooledEffect) {
            this.pooledEffect = pooledEffect;
        }

        @Override
        public void act(float delta) {
            super.act(delta);
            if (freed) {
                return;
            }
            if (pooledEffect.isComplete()) {
                // Return the effect to the pool; otherwise every fireball allocates a new particle effect.
                dispose();
                return;
            }
            pooledEffect.update(delta);
        }

        /** Leaves the stage and returns the effect to the pool exactly once, even if it is still smoking. */
        public void dispose() {
            remove();
            if (!freed) {
                freed = true;
                pooledEffect.free();
            }
        }

        @Override
        public void draw(Batch batch, float parentAlpha) {
            super.draw(batch, parentAlpha);
            if (!freed) {
                pooledEffect.draw(batch);
            }
        }

        public ParticleEffectPool.PooledEffect getParticleEffect() {
            return pooledEffect;
        }

        /** Ignored once the effect is back in the pool, where another smoke may already own it. */
        public void moveTo(float x, float y) {
            if (!freed) {
                pooledEffect.setPosition(x, y);
            }
        }
    }
}
