package sk.sivak.eldritchhorror.core.view.action;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.MathUtils;
import sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager;
import sk.sivak.eldritchhorror.core.view.utils.FastForwardAction;
import sk.sivak.eldritchhorror.core.view.utils.GameplaySounds;

/** Distinct pickup and HUD arrival cues; all preloaded during startup. */
public final class TokenSounds {

    public enum Cue {
        // Health and Sanity have their own pickups but share the HUD arrival and loss cues.
        HEALTH_GAIN(CustomAssetManager.TOKEN_GAIN_SOUND, 0.90f),
        SANITY_GAIN(GameplaySounds.Cue.SANITY_GAIN.path, GameplaySounds.Cue.SANITY_GAIN.volume),
        CLUE_GAIN(GameplaySounds.Cue.CLUE_GAIN.path, GameplaySounds.Cue.CLUE_GAIN.volume),
        FOCUS_GAIN(GameplaySounds.Cue.FOCUS_GAIN.path, GameplaySounds.Cue.FOCUS_GAIN.volume),
        TICKET_GAIN(GameplaySounds.Cue.TICKET_GAIN.path, GameplaySounds.Cue.TICKET_GAIN.volume),
        CLUE_LAND(GameplaySounds.Cue.CLUE_LAND.path, GameplaySounds.Cue.CLUE_LAND.volume),
        FOCUS_LAND(GameplaySounds.Cue.FOCUS_LAND.path, GameplaySounds.Cue.FOCUS_LAND.volume),
        TICKET_LAND(GameplaySounds.Cue.TICKET_LAND.path, GameplaySounds.Cue.TICKET_LAND.volume),
        LOSS(CustomAssetManager.TOKEN_LOSS_SOUND, 0.90f),
        TOKEN_LEAVE(CustomAssetManager.TOKEN_LEAVE_SOUND, 0.85f),
        TOKEN_LAND(CustomAssetManager.TOKEN_LAND_SOUND, 0.90f);

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

    /** Play only when the token reaches its HUD slot, using a shorter matching contact sound. */
    public static void playLand(Cue gain, int tokenIndex) {
        Cue landing = gain == Cue.CLUE_GAIN ? Cue.CLUE_LAND
                : gain == Cue.FOCUS_GAIN ? Cue.FOCUS_LAND
                : gain == Cue.TICKET_GAIN ? Cue.TICKET_LAND : Cue.TOKEN_LAND;
        playGain(landing, tokenIndex);
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
