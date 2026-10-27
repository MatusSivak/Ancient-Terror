package sk.sivak.eldritchhorror.core.view.map.monster;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;

/** A centre-pivot side turn, independent of breathing, walking and attack scale. */
public final class MonsterFacing {
    private static final float TURN_DURATION = .24f;
    private final boolean artworkFacesRight;
    private boolean facingRight;
    private float from = 1f, scale = 1f, target = 1f, elapsed = TURN_DURATION;

    public MonsterFacing(boolean artworkFacesRight) {
        this.artworkFacesRight = artworkFacesRight;
        facingRight = artworkFacesRight;
    }

    public void face(boolean right, boolean animate) {
        if (right == facingRight && animate) return;
        facingRight = right;
        from = scale;
        target = right == artworkFacesRight ? 1f : -1f;
        elapsed = animate ? 0f : TURN_DURATION;
        if (!animate) scale = target;
    }

    /** Ignore horizontal jitter; vertical movement preserves the last heading. */
    public void followMovement(float dx) {
        if (Math.abs(dx) > .05f) face(dx > 0f, true);
    }

    public void update(float delta) {
        elapsed = Math.min(TURN_DURATION, elapsed + Math.max(0f, delta));
        scale = MathUtils.lerp(from, target, Interpolation.sine.apply(elapsed / TURN_DURATION));
    }

    public float scaleX() {
        // A zero actor scale breaks Scene2D's inverse transform for hit testing.
        return Math.abs(scale) < .04f ? Math.copySign(.04f, scale == 0f ? target : scale) : scale;
    }
}
