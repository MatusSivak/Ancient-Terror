package sk.sivak.eldritchhorror.core.view.utils;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Plays short sound effects without stalling the render thread.
 * On Android {@code SoundPool.play()} is a blocking binder call that can take several ms per call,
 * so bursts (fireballs, tokens) drop frames; there the call runs on a single background thread.
 * Desktop OpenAL is not thread-safe, so it plays inline. Identical sounds within a few ms are merged.
 */
public final class SoundPlayer {

    private static final long MIN_REPEAT_MILLIS = 20;
    private static final Map<Sound, Long> lastPlayed = new HashMap<>();
    private static ExecutorService executor;

    private SoundPlayer() {
    }

    public static void play(Sound sound, float volume, float pitch, float pan) {
        play(sound, volume, pitch, pan, true);
    }

    /** @param mergeRepeats false for cues that should stack, e.g. several dice landing together */
    public static void play(Sound sound, float volume, float pitch, float pan, boolean mergeRepeats) {
        if (sound == null) {
            return;
        }
        if (mergeRepeats) {
            long now = TimeUtils.millis();
            Long last = lastPlayed.get(sound);
            if (last != null && now - last < MIN_REPEAT_MILLIS) {
                return;
            }
            lastPlayed.put(sound, now);
        }
        if (Gdx.app == null || Gdx.app.getType() != Application.ApplicationType.Android) {
            sound.play(volume, pitch, pan);
            return;
        }
        executor().execute(() -> {
            try {
                sound.play(volume, pitch, pan);
            } catch (RuntimeException e) {
                Gdx.app.error("SoundPlayer", "play failed", e);
            }
        });
    }

    /** Runs a short, thread-safe platform call (e.g. vibration) off the render thread on Android. */
    public static void runInBackground(Runnable task) {
        if (Gdx.app == null || Gdx.app.getType() != Application.ApplicationType.Android) {
            task.run();
            return;
        }
        executor().execute(task);
    }

    private static synchronized ExecutorService executor() {
        if (executor == null) {
            executor = Executors.newSingleThreadExecutor(runnable -> {
                Thread thread = new Thread(runnable, "sound-player");
                thread.setDaemon(true);
                return thread;
            });
        }
        return executor;
    }
}
