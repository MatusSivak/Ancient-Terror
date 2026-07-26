package sk.sivak.eldritchhorror.core.view.components.lightning;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;

public class CompositeBoltAnimationActor extends Actor {

    private float time = 0f;
    private final CompositeBoltAnimation compositeBoltAnimation;
    private CompositeBolt currentFrame;

    private ParticleEffectPool effectPool;
    private Array<ParticleEffectPool.PooledEffect> effects = new Array();

    private Color explosionColor = Color.WHITE;
    private float explosionScale = 0.8f;

    public CompositeBoltAnimationActor(CompositeBoltAnimation compositeBoltAnimation) {
        this.compositeBoltAnimation = compositeBoltAnimation;

        ParticleEffect particleEffect = new ParticleEffect();
        particleEffect.load(Gdx.files.internal("particle/explosion.p"), Gdx.files.internal("particle"));
        particleEffect.start();
        effectPool = new ParticleEffectPool(particleEffect, 3, 5);
        time = MathUtils.random();
        currentFrame = compositeBoltAnimation.getKeyFrame(time);
    }

    public void setExplosionColor(Color explosionColor) {
        this.explosionColor = explosionColor;
    }

    public void setExplosionScale(float explosionScale) {
        this.explosionScale = explosionScale;
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        time += delta * MathUtils.random(0.5f, 1.5f);
        CompositeBolt keyFrame = compositeBoltAnimation.getKeyFrame(time);
        if (keyFrame != currentFrame) {
            keyFrame.init();
            ParticleEffectPool.PooledEffect pooledEffect = effectPool.obtain();
            pooledEffect.start();
            pooledEffect.setPosition(keyFrame.getStart().x, keyFrame.getStart().y);
            pooledEffect.scaleEffect(explosionScale);
            pooledEffect.getEmitters().get(0).getTint().setColors(
                    new float[]{explosionColor.r, explosionColor.g, explosionColor.b, explosionColor.a});
            effects.add(pooledEffect);
        }


        for (int i = effects.size - 1; i >= 0; i--) {
            ParticleEffectPool.PooledEffect pooledEffect = effects.get(i);
            pooledEffect.update(delta);
            if (pooledEffect.isComplete()) {
                pooledEffect.free();
                effects.removeIndex(i);
            }
        }

        currentFrame = keyFrame;
        currentFrame.act(delta);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        currentFrame.draw(batch, parentAlpha * getColor().a);

        for (ParticleEffectPool.PooledEffect effect : effects) {
            effect.draw(batch);
        }
    }
}
