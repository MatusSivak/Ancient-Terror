package sk.sivak.eldritchhorror.core.view.map.gate;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction;
import rx.CompletableSubscriber;
import sk.sivak.eldritchhorror.core.constants.ViewProperties;
import sk.sivak.eldritchhorror.core.view.utils.FastForwardAction;

import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.GATE_SMOKE;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.getTexture;

/**
 * A closing gate shudders, its vortex collapses inward and the energy drifts away as glowing mist.
 * One clock drives every layer so the effect stays coherent at fast-forward speed.
 */
final class GateDissipateAnimation extends Group {
    static final float SPIN_UP_TIME = 0.8f;
    private static final float FADE_TIME = 1.2f;
    private static final float TOTAL_TIME = SPIN_UP_TIME + FADE_TIME;
    private static final int WISPS = 30;
    /** Degrees per second of the open gate (360 degrees per 8 seconds in NewGateAnimatedImage). */
    static final float IDLE_SPIN = 45f;
    static final float CORE_PEAK_SPIN = 600f;
    static final float HALO_PEAK_SPIN = 260f;

    private final Group portrait;
    private final Actor halo;
    private final Actor core;
    private final float haloScale, coreScale, haloAlpha, haloRotation, coreRotation;
    private final Mist mist;

    static void start(Group portrait, Actor halo, Actor core, Color accent, String name, CompletableSubscriber subscriber) {
        Stage stage = portrait.getStage();
        portrait.remove();
        GateDissipateAnimation animation = new GateDissipateAnimation(portrait, halo, core, accent);
        animation.setName(name);
        stage.addActor(animation);
        animation.addAction(new FastForwardAction<>(new TemporalAction(TOTAL_TIME * ViewProperties.NORMAL_ACTION_DURATION) {
            @Override protected void update(float percent) {
                animation.animate(percent * TOTAL_TIME);
            }
            @Override protected void end() {
                animation.remove();
                subscriber.onCompleted();
            }
        }));
    }

    private GateDissipateAnimation(Group portrait, Actor halo, Actor core, Color accent) {
        this.portrait = portrait;
        this.halo = halo;
        this.core = core;
        setTransform(false);
        setTouchable(Touchable.disabled);
        haloScale = halo.getScaleX();
        coreScale = core.getScaleX();
        haloAlpha = halo.getColor().a;
        haloRotation = halo.getRotation();
        coreRotation = core.getRotation();
        addActor(portrait);
        Vector2 center = portrait.localToStageCoordinates(new Vector2(portrait.getWidth() / 2f, portrait.getHeight() / 2f));
        // The vortex itself is about a third of the portrait; the halo reaches further.
        float radius = core.getWidth() * coreScale * portrait.getScaleX() * 0.36f;
        mist = new Mist(center, radius, new Color(accent).lerp(Color.WHITE, 0.3f));
        mist.setName("gate-mist");
        addActor(mist);
    }

    private void animate(float elapsed) {
        core.setRotation(coreRotation + spinDegrees(elapsed, CORE_PEAK_SPIN));
        halo.setRotation(haloRotation + spinDegrees(elapsed, HALO_PEAK_SPIN));
        if (elapsed < SPIN_UP_TIME) {
            float progress = elapsed / SPIN_UP_TIME;
            float tremor = MathUtils.sin(progress * MathUtils.PI);
            // The vortex spins up and swells as the seal takes hold.
            core.setScale(coreScale * (1f + 0.12f * tremor));
            halo.setScale(haloScale * (1f + 0.06f * tremor));
            halo.getColor().a = MathUtils.lerp(haloAlpha, 1f, progress);
            mist.update(0f);
        } else {
            float progress = Math.min((elapsed - SPIN_UP_TIME) / FADE_TIME, 1f);
            portrait.setRotation(0f);
            core.setScale(coreScale * MathUtils.lerp(1f, 0.08f, Interpolation.pow2In.apply(Math.min(progress / 0.7f, 1f))));
            core.getColor().a = 1f - Interpolation.pow2In.apply(Math.min(progress / 0.7f, 1f));
            halo.setScale(haloScale * (1f + 0.35f * Interpolation.pow2Out.apply(progress)));
            halo.getColor().a = 1f - Interpolation.pow2Out.apply(Math.min(progress / 0.6f, 1f));
            mist.update(progress);
        }
    }

    /**
     * Starts at the open gate's idle speed, in its idle direction, and accelerates
     * to full speed while still opaque, then holds that speed throughout dissipation.
     */
    static float spinDegrees(float elapsed, float peakSpeed) {
        float time = MathUtils.clamp(elapsed, 0f, TOTAL_TIME);
        float ramp = Math.min(time, SPIN_UP_TIME);
        float extra = peakSpeed - IDLE_SPIN;
        // Integral of a smoothstep velocity ramp; no jump when the mist begins.
        float p = ramp / SPIN_UP_TIME;
        return IDLE_SPIN * ramp + extra * SPIN_UP_TIME * (p * p * p - .5f * p * p * p * p)
                + peakSpeed * Math.max(0f, time - SPIN_UP_TIME);
    }

    @Override protected void setStage(Stage stage) {
        super.setStage(stage);
        if (stage == null) clearActions();
    }

    /** Additive puffs of the gate's color, drawn in one blend-state change. */
    private static final class Mist extends Actor {
        private final Texture texture = getTexture(GATE_SMOKE);
        private final Vector2 center;
        private final float radius;
        private final Color tint;
        private final float[] angle = new float[WISPS], delay = new float[WISPS], drift = new float[WISPS],
                size = new float[WISPS], spin = new float[WISPS], sway = new float[WISPS];
        private float progress;

        Mist(Vector2 center, float radius, Color tint) {
            this.center = center;
            this.radius = radius;
            this.tint = tint;
            setTouchable(Touchable.disabled);
            for (int i = 0; i < WISPS; i++) {
                angle[i] = (360f / WISPS) * i + MathUtils.random(-6f, 6f);
                delay[i] = MathUtils.random(0f, 0.5f);
                drift[i] = MathUtils.random(0.7f, 1.3f);
                size[i] = MathUtils.random(0.35f, 0.8f);
                spin[i] = MathUtils.random(-70f, 70f);
                sway[i] = MathUtils.random(0f, MathUtils.PI2);
            }
        }

        void update(float progress) {
            this.progress = progress;
        }

        @Override public void draw(Batch batch, float parentAlpha) {
            if (progress <= 0f) return;
            int src = batch.getBlendSrcFunc(), dst = batch.getBlendDstFunc();
            batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE);
            for (int i = 0; i < WISPS; i++) {
                float life = MathUtils.clamp((progress - delay[i]) / (1f - delay[i]), 0f, 1f);
                if (life <= 0f) continue;
                float travel = Interpolation.pow2Out.apply(life);
                float distance = radius * (0.6f + 0.3f * size[i] + 1.2f * drift[i] * travel);
                // Mist rises like warm air and wavers as it thins out.
                float swirl = angle[i] + 155f * travel;
                float x = center.x + MathUtils.cosDeg(swirl) * distance
                        + MathUtils.sin(sway[i] + life * 5f) * radius * 0.12f * life;
                float y = center.y + MathUtils.sinDeg(swirl) * distance + radius * 1.6f * drift[i] * life * life;
                float puff = radius * size[i] * (0.45f + 0.9f * travel);
                float alpha = Math.min(life / 0.15f, 1f) * (1f - Interpolation.pow2In.apply(life)) * 0.42f;
                batch.setColor(tint.r, tint.g, tint.b, alpha * parentAlpha);
                batch.draw(texture, x - puff, y - puff, puff, puff, puff * 2f, puff * 2f, 1f, 1f,
                        spin[i] * life + angle[i], 0, 0, texture.getWidth(), texture.getHeight(), false, false);
            }
            batch.setBlendFunction(src, dst);
            batch.setColor(Color.WHITE);
        }
    }
}
