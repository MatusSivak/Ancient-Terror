package sk.sivak.eldritchhorror.core.view.components.diceroller;

/** Normalized throw curves: flight, two smaller rebounds, then a short slide. */
final class DiceThrowMotion {
    private DiceThrowMotion() { }

    static float releaseDelay(int index, int count) {
        return count <= 1 ? 0f : 0.18f * index / (count - 1f);
    }

    static float wobble(float progress) {
        if (progress <= 0.90f || progress >= 1f) return 0f;
        float t = (progress - 0.90f) / 0.10f;
        return (float)Math.sin(t * Math.PI * 4) * (1f - t) * (1f - t);
    }

    static float impactTime(int index) {
        if (index == 0) return 0.52f;
        if (index == 1) return 0.76f;
        if (index == 2) return 0.90f;
        throw new IllegalArgumentException("Unknown dice impact: " + index);
    }

    static float height(float progress) {
        if (progress < 0.52f) return arc(progress / 0.52f);
        if (progress < 0.76f) return 0.24f * arc((progress - 0.52f) / 0.24f);
        if (progress < 0.90f) return 0.065f * arc((progress - 0.76f) / 0.14f);
        return 0f;
    }

    private static float arc(float t) {
        return 4f * t * (1f - t);
    }

    static float travel(float progress) {
        float remaining = 1f - progress;
        return 1f - remaining * remaining;
    }

    static float tumble(float progress) {
        // Each impact sheds angular velocity; the final face is down before sliding stops.
        if (progress < 0.52f) return 0.72f * progress / 0.52f;
        if (progress < 0.76f) return 0.72f + 0.21f * (progress - 0.52f) / 0.24f;
        if (progress < 0.90f) return 0.93f + 0.07f * (progress - 0.76f) / 0.14f;
        return 1f;
    }
}
