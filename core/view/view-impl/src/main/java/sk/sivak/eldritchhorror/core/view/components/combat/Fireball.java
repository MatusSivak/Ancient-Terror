package sk.sivak.eldritchhorror.core.view.components.combat;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction;
import com.badlogic.gdx.utils.Align;
import sk.sivak.eldritchhorror.core.view.shader.SceneLight;
import sk.sivak.eldritchhorror.core.view.utils.FastForwardAction;

import java.util.LinkedList;
import java.util.List;

public class Fireball extends FireballImage {

    private List<Runnable> onLandActions = new LinkedList<>();
    private FireballSmokeBuilder.FireballSmoke fireballSmoke;

    private Explosion explosion;

    private Group smokeGroup;
    private float midpointDisplacementDirection;
    private float sizeMultiplier = 1f;
    private static final Color FIRE_LIGHT = new Color(1f, 0.5f, 0.16f, 1f);
    private static final Color HORROR_LIGHT = new Color(0.28f, 0.5f, 1f, 1f);
    private static final float LIGHT_RADIUS = 300f;
    private SceneLight light;
    private final Vector2 lightPosition = new Vector2();

    public Fireball() {
        super(Kind.DAMAGE);
        setSize(256, 256);
        setOrigin(Align.center);
    }

    public void setSmokeGroup(Group smokeGroup) {
        this.smokeGroup = smokeGroup;
    }

    public void setExplosion(Explosion explosion) {
        this.explosion = explosion;
    }

    public void addOnLandAction(Runnable onLandAction) {
        onLandActions.add(onLandAction);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        if (fireballSmoke != null) {
            fireballSmoke.moveTo(getX() + getWidth()/2f, getY() + getHeight()/2f);
        }
        updateLight();
    }

    /** The flame lights the background around it; it glows brighter as it grows and heats up to full color. */
    private void updateLight() {
        if (getStage() == null) {
            return;
        }
        if (light == null) {
            light = SceneLight.add(getStage(), blue ? HORROR_LIGHT : FIRE_LIGHT, LIGHT_RADIUS * sizeMultiplier);
        }
        localToStageCoordinates(lightPosition.set(getWidth() / 2f, getHeight() / 2f));
        float growth = MathUtils.clamp(getScaleX() / (0.5f * sizeMultiplier), 0f, 1.2f);
        float flicker = 0.9f + 0.1f * MathUtils.sin(stateTime * 31f) * MathUtils.sin(stateTime * 17f);
        light.setPosition(lightPosition.x, lightPosition.y)
                .setIntensity(isVisible() ? 1.1f * growth * getColor().r * flicker : 0f);
    }

    @Override
    protected void setStage(Stage stage) {
        super.setStage(stage);
        if (stage == null && light != null) {
            light.remove();
            light = null;
        }
    }

    public void setBlue(boolean blue) {
        this.blue = blue;
    }

    public void setSizeMultiplier(float sizeMultiplier) {
        this.sizeMultiplier = sizeMultiplier;
    }

    public void setMidpointDisplacementDirection(float direction) {
        this.midpointDisplacementDirection = direction;
    }

    void throwIt(Vector2 origin, Vector2 destination, boolean intercepted) {

        Vector2 start = new Vector2(origin);
        Vector2 aim = new Vector2(destination);
        fireballSmoke = FireballSmokeBuilder.get();
        smokeGroup.addActor(fireballSmoke);
        setPosition(
                origin.x - getWidth()/2f,
                origin.y - getHeight()/2f);
        setScale(0.0f);
        CombatSounds.playMonsterFireballLaunch();

        float velocity = MathUtils.random(450, 520);
        Vector2 tangent = new Vector2(destination).sub(origin);
        float duration = tangent.len() / velocity;
        float travelFraction = intercepted ? 0.5f : 1f;

        float scale = MathUtils.random(0.4f, 0.6f) * sizeMultiplier;
        setColor(Color.DARK_GRAY);
        Vector2 bend = new Vector2(tangent.y, -tangent.x).scl(midpointDisplacementDirection / 10f);
        float flightTime = intercepted ? Math.max(duration * travelFraction, BulletTracer.FLIGHT) : duration;
        TemporalAction flight = new TemporalAction(flightTime) {
            private boolean fired;

            @Override
            protected void update(float progress) {
                float distance = Interpolation.sine.apply(progress * travelFraction);
                float curve = MathUtils.sin(MathUtils.PI * distance);
                setPosition(start.x + tangent.x * distance + bend.x * curve,
                        start.y + tangent.y * distance + bend.y * curve, Align.center);
                if (intercepted && !fired) {
                    float shotAge = BulletTracer.FLIGHT - (1f - progress) * flightTime;
                    if (shotAge >= 0f) {
                        // At the midpoint the curve is at its full displacement.
                        Vector2 impact = new Vector2(start).mulAdd(tangent, 0.5f).add(bend);
                        BulletTracer.fire(getParent(), aim, impact, shotAge);
                        fired = true;
                    }
                }
            }

            @Override
            protected void end() {
                land(aim, intercepted);
            }
        };
        SequenceAction color = Actions.sequence(Actions.color(Color.WHITE, duration / 2f));
        SequenceAction size = Actions.sequence(Actions.scaleTo(scale, scale, duration / 2f, Interpolation.sineOut));
        if (!intercepted) {
            color.addAction(Actions.color(Color.DARK_GRAY, duration / 2f));
            size.addAction(Actions.scaleTo(scale * 0.1f, scale * 0.1f, duration / 2f, Interpolation.sineIn));
        }
        addAction(new FastForwardAction<>(Actions.sequence(
                Actions.parallel(color, size, flight,
                        Actions.rotateTo(MathUtils.random(-360, 360) * travelFraction, flightTime)),
                Actions.removeActor()
        )));
    }

    private void land(Vector2 aim, boolean intercepted) {
        Vector2 impact = new Vector2(getX(Align.center), getY(Align.center));
        Color burstColor = blue ? new Color(0.25f, 0.55f, 1f, 1f) : new Color(1f, 0.3f, 0.12f, 1f);
        if (intercepted) {
            setVisible(false);
            FireballShatter.burst(getParent(), impact, new Vector2(impact).sub(aim).nor(), burstColor, sizeMultiplier);
        }
        CombatSounds.playFireballImpact();
        if (!intercepted && !Gdx.app.getPreferences("AncientTerror.xml").getBoolean("vibration_disabled", false)) {
            sk.sivak.eldritchhorror.core.view.utils.SoundPlayer.runInBackground(() -> Gdx.input.vibrate(50));
        }
        fireballSmoke.getParticleEffect().allowCompletion();
        explosion.start(new Explosion.ExplosionConfig(impact, burstColor));
        SceneLight.flash(getParent(), impact, blue ? HORROR_LIGHT : FIRE_LIGHT,
                LIGHT_RADIUS * 1.4f * sizeMultiplier, 1.6f, 0.08f, 0f, 0.5f);
        for (Runnable onLandAction : onLandActions) {
            onLandAction.run();
        }
    }
}
