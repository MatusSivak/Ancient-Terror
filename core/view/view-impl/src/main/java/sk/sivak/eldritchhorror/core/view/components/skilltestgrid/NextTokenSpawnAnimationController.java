package sk.sivak.eldritchhorror.core.view.components.skilltestgrid;

final class NextTokenSpawnAnimationController {
    static final float SPAWN_DURATION = 0.26f;
    static final float SETTLE_DURATION = 0.12f;
    static final float TOTAL_DURATION = SPAWN_DURATION + SETTLE_DURATION;

    enum Phase {
        IDLE,
        SPAWNING,
        SETTLING
    }

    private SymbolType currentNextToken;
    private SymbolType previousNextToken;
    private Phase phase = Phase.IDLE;
    private float elapsed;

    boolean setNextToken(SymbolType nextToken) {
        if (currentNextToken == nextToken) {
            return false;
        }

        previousNextToken = currentNextToken;
        currentNextToken = nextToken;
        elapsed = 0f;
        phase = nextToken == null ? Phase.IDLE : Phase.SPAWNING;
        return true;
    }

    void update(float delta) {
        if (phase == Phase.IDLE || delta <= 0f) {
            return;
        }

        elapsed = Math.min(TOTAL_DURATION, elapsed + delta);
        if (elapsed < SPAWN_DURATION) {
            phase = Phase.SPAWNING;
        } else if (elapsed < TOTAL_DURATION) {
            phase = Phase.SETTLING;
        } else {
            phase = Phase.IDLE;
        }
    }

    SymbolType getCurrentNextToken() {
        return currentNextToken;
    }

    SymbolType getPreviousNextToken() {
        return previousNextToken;
    }

    Phase getPhase() {
        return phase;
    }

    float getTokenAlpha() {
        if (phase == Phase.IDLE) {
            return currentNextToken == null ? 0f : 1f;
        }
        float progress = clamp((elapsed - 0.04f) / (SPAWN_DURATION - 0.04f));
        return smoothStep(progress);
    }

    float getTokenScale() {
        if (phase == Phase.IDLE) {
            return 1f;
        }
        if (phase == Phase.SPAWNING) {
            return lerp(0.84f, 1.07f, smoothStep(elapsed / SPAWN_DURATION));
        }
        float settleProgress = (elapsed - SPAWN_DURATION) / SETTLE_DURATION;
        return lerp(1.07f, 1f, smoothStep(settleProgress));
    }

    float getEffectAlpha() {
        if (phase == Phase.IDLE) {
            return 0f;
        }
        float effectProgress = clamp(elapsed / 0.32f);
        return (float) Math.sin(Math.PI * effectProgress) * 0.9f;
    }

    float getEffectProgress() {
        return clamp(elapsed / 0.32f);
    }

    private static float smoothStep(float value) {
        float clamped = clamp(value);
        return clamped * clamped * (3f - 2f * clamped);
    }

    private static float lerp(float start, float end, float progress) {
        return start + (end - start) * clamp(progress);
    }

    private static float clamp(float value) {
        return Math.max(0f, Math.min(1f, value));
    }
}
