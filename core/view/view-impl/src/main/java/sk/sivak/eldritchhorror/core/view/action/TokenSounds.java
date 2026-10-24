package sk.sivak.eldritchhorror.core.view.action;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.MathUtils;
import sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager;
import sk.sivak.eldritchhorror.core.view.utils.FastForwardAction;
import sk.sivak.eldritchhorror.core.view.utils.GameplaySounds;

/** Health/Sanity cues plus distinct clue and focus pickups; all preloaded during startup. */
public final class TokenSounds {

    public enum Cue {
        // Health and Sanity share the same cues.
        GAIN(CustomAssetManager.TOKEN_GAIN_SOUND, 0.55f),
        CLUE_GAIN(GameplaySounds.Cue.CLUE_GAIN.path, GameplaySounds.Cue.CLUE_GAIN.volume),
        FOCUS_GAIN(GameplaySounds.Cue.FOCUS_GAIN.path, GameplaySounds.Cue.FOCUS_GAIN.volume),
        LOSS(CustomAssetManager.TOKEN_LOSS_SOUND, 0.60f),
        TOKEN_LEAVE(CustomAssetManager.TOKEN_LEAVE_SOUND, 0.50f),
        TOKEN_LAND(CustomAssetManager.TOKEN_LAND_SOUND, 0.45f);

        public final String path;
        private final float volume;

        Cue(String path, float volume) {
            this.path = path;
            this.volume = volume;
        }
    }

    private TokenSounds() {
    }

    /** One cue per gained token; each following token in the same gain sounds a little higher. */
    public static void playGain(Cue cue, int tokenIndex) {
        play(cue, cue.volume, 1f + Math.min(tokenIndex, 4) * 0.03f);
    }

    /** One cue per loss, slightly louder when several tokens are lost at once. */
    public static void playLoss(Cue cue, int amount) {
        play(cue, Math.min(1f, cue.volume * (1f + 0.12f * (Math.max(1, amount) - 1))), 1f);
    }

    /** Gained Health/Sanity token reaching its HUD slot; climbs with the gain cue. */
    public static void playLand(int tokenIndex) {
        playGain(Cue.TOKEN_LAND, tokenIndex);
    }

    /** Health/Sanity token(s) leaving the HUD bar; once per loss, not per token. */
    public static void playLeave() {
        play(Cue.TOKEN_LEAVE, Cue.TOKEN_LEAVE.volume, 1f);
    }

    private static void play(Cue cue, float volume, float pitch) {
        // Fast-forward shrinks the animations to a few frames; skip the cue instead of stacking it.
        if (FastForwardAction.isOn()) {
            return;
        }
        Sound sound = CustomAssetManager.getLoadedSound(cue.path);
        sk.sivak.eldritchhorror.core.view.utils.SoundPlayer.play(sound, volume, pitch * MathUtils.random(0.98f, 1.02f), 0f);
    }
}
