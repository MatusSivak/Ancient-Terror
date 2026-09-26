package sk.sivak.eldritchhorror.core.view.components.track;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.MathUtils;
import sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager;
import sk.sivak.eldritchhorror.core.view.utils.FastForwardAction;
import sk.sivak.eldritchhorror.core.view.utils.SoundPlayer;

/** Optional cues played when a HUD track zooms to the center of the screen and back. */
final class TrackZoomSounds {

    private static final float VOLUME = 0.5f;

    private TrackZoomSounds() {
    }

    static void play(String path) {
        // Fast-forward shrinks the zoom to a few frames; skip the cue instead of stacking it.
        if (path == null || FastForwardAction.isOn()) {
            return;
        }
        Sound sound = CustomAssetManager.getLoadedSound(path);
        if (sound == null) {
            sound = CustomAssetManager.getSound(path);
        }
        SoundPlayer.play(sound, VOLUME, MathUtils.random(0.98f, 1.02f), 0f);
    }
}
