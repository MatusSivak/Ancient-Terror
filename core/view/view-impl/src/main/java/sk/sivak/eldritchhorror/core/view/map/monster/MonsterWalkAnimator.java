package sk.sivak.eldritchhorror.core.view.map.monster;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;

/**
 * Makes a map monster look like it walks instead of gliding: while its position changes it bounces with each step and
 * waddles from side to side. Steps follow the distance travelled, so slow and fast moves both look natural.
 * Only the drawing is offset; the actor's real position is never touched.
 */
class MonsterWalkAnimator {

    /** Distance of one step, relative to the monster height. */
    private static final float STRIDE = 0.3f;
    private static final float MAX_STEPS_PER_SECOND = 4.5f;
    private static final float BOB_HEIGHT = 0.08f;
    private static final float TILT_DEGREES = 6f;
    private static final float BLEND_DURATION = 0.15f;

    private final Vector2 last = new Vector2();
    private boolean hasLast;
    private float phase;
    private float blend;

    void update(Actor actor, float delta) {
        float x = actor.getX();
        float y = actor.getY();
        boolean walking = false;
        if (hasLast && delta > 0f) {
            float moved = Vector2.dst(last.x, last.y, x, y);
            // Ignore jitter and the jump when the map wraps around.
            walking = moved > 0.05f && moved < actor.getWidth();
            if (walking) {
                float steps = Math.min(moved / (actor.getHeight() * STRIDE), MAX_STEPS_PER_SECOND * delta);
                phase += steps * MathUtils.PI;
            }
        }
        blend = MathUtils.clamp(blend + (walking ? delta : -delta) / BLEND_DURATION, 0f, 1f);
        if (blend == 0f) {
            phase = 0f;
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
        return MathUtils.sin(phase) * TILT_DEGREES * blend;
    }
}
