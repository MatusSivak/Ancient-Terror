package sk.sivak.eldritchhorror.core.view.map.monster;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;

/**
 * Makes a map monster look like it walks instead of gliding: while its position changes it bounces with each step,
 * waddles from side to side, leans into its direction of travel and squashes slightly when a foot lands.
 * Steps follow the distance travelled, so slow and fast moves both look natural.
 * Only the drawing is offset; the actor's real position is never touched.
 */
class MonsterWalkAnimator {

    /** Distance of one step, relative to the monster height. */
    private static final float STRIDE = 0.3f;
    private static final float MAX_STEPS_PER_SECOND = 4.5f;
    private static final float BOB_HEIGHT = 0.08f;
    private static final float TILT_DEGREES = 5f;
    private static final float LEAN_DEGREES = 7f;
    /** Horizontal speed (in monster heights per second) at which the lean is strongest. */
    private static final float FULL_LEAN_SPEED = 2.5f;
    /** How quickly the lean follows the speed; higher is snappier. */
    private static final float LEAN_RESPONSE = 6f;
    private static final float LANDING_SQUASH = 0.06f;
    private static final float BLEND_IN_DURATION = 0.12f;
    private static final float BLEND_OUT_DURATION = 0.3f;

    private final Vector2 last = new Vector2();
    private boolean hasLast;
    private float phase;
    private float blend;
    private float lean;

    private float savedY;
    private float savedRotation;
    private float savedScaleX;
    private float savedScaleY;
    private boolean applied;

    void update(Actor actor, float delta) {
        float x = actor.getX();
        float y = actor.getY();
        boolean walking = false;
        float targetLean = 0f;
        if (hasLast && delta > 0f) {
            float moved = Vector2.dst(last.x, last.y, x, y);
            // Ignore jitter and the jump when the map wraps around.
            walking = moved > 0.05f && moved < actor.getWidth();
            if (walking) {
                float steps = Math.min(moved / (actor.getHeight() * STRIDE), MAX_STEPS_PER_SECOND * delta);
                phase += steps * MathUtils.PI;
                float speedX = (x - last.x) / delta / actor.getHeight();
                // Positive rotation is counter-clockwise, so walking right leans the top to the right.
                targetLean = -MathUtils.clamp(speedX / FULL_LEAN_SPEED, -1f, 1f) * LEAN_DEGREES;
            }
        }
        float blendStep = walking ? delta / BLEND_IN_DURATION : -delta / BLEND_OUT_DURATION;
        blend = MathUtils.clamp(blend + blendStep, 0f, 1f);
        if (delta > 0f) {
            lean += (targetLean - lean) * Math.min(1f, delta * LEAN_RESPONSE);
        }
        if (blend == 0f) {
            phase = 0f;
            lean = 0f;
        }
        last.set(x, y);
        hasLast = true;
    }

    boolean isActive() {
        return blend > 0f;
    }

    float getBob(float height) {
        return Math.abs(MathUtils.sin(phase)) * height * BOB_HEIGHT * blend;
    }

    float getTilt() {
        return (MathUtils.sin(phase) * TILT_DEGREES + lean) * blend;
    }

    /** Strongest right when a foot touches the ground, i.e. at the bottom of the bounce. */
    float getSquash() {
        float contact = 1f - Math.abs(MathUtils.sin(phase));
        return contact * contact * contact * LANDING_SQUASH * blend;
    }

    /** Offsets the actor for drawing; must be paired with {@link #restore(Actor)}. */
    void apply(Actor actor) {
        applied = isActive();
        if (!applied) {
            return;
        }
        savedY = actor.getY();
        savedRotation = actor.getRotation();
        savedScaleX = actor.getScaleX();
        savedScaleY = actor.getScaleY();
        float squash = getSquash();
        float scaleY = savedScaleY * (1f - squash);
        // Scaling happens around the centre; drop the body so its feet stay on the ground.
        float keepFeetDown = actor.getOriginY() * (savedScaleY - scaleY);
        actor.setY(savedY + getBob(actor.getHeight()) - keepFeetDown);
        actor.setRotation(savedRotation + getTilt());
        actor.setScale(savedScaleX * (1f + squash), scaleY);
    }

    void restore(Actor actor) {
        if (!applied) {
            return;
        }
        actor.setY(savedY);
        actor.setRotation(savedRotation);
        actor.setScale(savedScaleX, savedScaleY);
        applied = false;
    }
}
