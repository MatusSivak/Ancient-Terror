package sk.sivak.eldritchhorror.core.view.components.skilltestgrid;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.utils.Disposable;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.LongSupplier;

final class GridTestAudio implements Disposable {
    private enum Channel {
        UI, ACTION, EXPLOSION, IMPLOSION, SPAWN, REWARD, CASCADE, LIFECYCLE
    }

    enum Cue {
        SELECT("swap_select", Channel.UI, 0.42f, 50),
        CANCEL("swap_deselect", Channel.UI, 0.40f, 50),
        UNAVAILABLE("action_unavailable", Channel.UI, 0.50f, 100),
        SETTING("grid_setting", Channel.UI, 0.35f, 60),
        SHIFT("shift", Channel.ACTION, 0.55f, 50),
        SWAP("swap", Channel.ACTION, 0.55f, 50),
        REROLL("grid_reroll", Channel.ACTION, 0.50f, 70),
        SUPER_REROLL("grid_super_reroll", Channel.ACTION, 0.50f, 100),
        PICKUP("grid_pickup", Channel.ACTION, 0.48f, 70),
        EXPLOSION("grid_explosion", Channel.EXPLOSION, 0.50f, 100),
        IMPLOSION("grid_implosion", Channel.IMPLOSION, 0.48f, 100),
        SPAWN("grid_spawn", Channel.SPAWN, 0.40f, 100),
        BONUS_SHIFT("grid_bonus_shift", Channel.REWARD, 0.35f, 120),
        CASCADE("grid_cascade", Channel.CASCADE, 0.28f, 120),
        TEST_START("grid_test_start", Channel.LIFECYCLE, 0.45f, 100),
        TEST_COMPLETE("grid_test_complete", Channel.LIFECYCLE, 0.50f, 100);

        private final String path;
        private final Channel channel;
        private final float volume;
        private final long minimumIntervalNanos;

        Cue(String filename, Channel channel, float volume, long minimumIntervalMillis) {
            this.path = "sounds/" + filename + ".wav";
            this.channel = channel;
            this.volume = volume;
            this.minimumIntervalNanos = minimumIntervalMillis * 1_000_000L;
        }
    }

    private final Function<String, Sound> loader;
    private final LongSupplier clock;
    private final Map<Cue, Sound> sounds = new EnumMap<>(Cue.class);
    private final Map<Channel, Sound> activeChannels = new EnumMap<>(Channel.class);
    private final Map<Cue, Long> lastPlayed = new EnumMap<>(Cue.class);
    private boolean disposed;

    GridTestAudio() {
        this(path -> Gdx.audio.newSound(Gdx.files.internal(path)), System::nanoTime);
    }

    GridTestAudio(Function<String, Sound> loader, LongSupplier clock) {
        this.loader = Objects.requireNonNull(loader, "loader must not be null");
        this.clock = Objects.requireNonNull(clock, "clock must not be null");
    }

    void play(Cue cue) {
        Objects.requireNonNull(cue, "cue must not be null");
        if (disposed) {
            throw new IllegalStateException("GridTest audio is disposed");
        }
        long now = clock.getAsLong();
        Long previousTime = lastPlayed.get(cue);
        if (previousTime != null && now - previousTime < cue.minimumIntervalNanos) {
            return;
        }
        Sound sound = sounds.get(cue);
        if (sound == null) {
            sound = Objects.requireNonNull(loader.apply(cue.path), "Sound loader returned null");
            sounds.put(cue, sound);
        }
        if (cue.channel == Channel.ACTION) {
            stopChannel(Channel.UI);
        }
        stopChannel(cue.channel);
        sound.play(cue.volume);
        activeChannels.put(cue.channel, sound);
        lastPlayed.put(cue, now);
    }

    void playMatchWave(List<GridMatch> matches, int bonusShifts, boolean cascade) {
        if (matches.isEmpty()) {
            return;
        }
        boolean neutral = false;
        boolean scoring = false;
        for (GridMatch match : matches) {
            if (match.isScoringMatch()) {
                scoring = true;
            } else {
                neutral = true;
            }
        }
        if (neutral) {
            play(Cue.EXPLOSION);
        }
        if (scoring) {
            play(Cue.IMPLOSION);
        }
        if (bonusShifts > 0) {
            play(Cue.BONUS_SHIFT);
        }
        if (cascade) {
            play(Cue.CASCADE);
        }
    }

    private void stopChannel(Channel channel) {
        Sound sound = activeChannels.remove(channel);
        if (sound != null) {
            sound.stop();
        }
    }

    void stopAll() {
        for (Sound sound : activeChannels.values()) {
            sound.stop();
        }
        activeChannels.clear();
        lastPlayed.clear();
    }

    @Override
    public void dispose() {
        stopAll();
        for (Sound sound : sounds.values()) {
            sound.dispose();
        }
        sounds.clear();
        disposed = true;
    }
}
