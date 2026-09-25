package sk.sivak.eldritchhorror.core.view.components.typewriter;

import com.badlogic.gdx.audio.Sound;
import java.util.Random;
import java.util.function.Function;
import java.util.function.LongSupplier;
import sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager;

/** Preloaded real key recordings. The character callback never loads an asset. */
final class TypewriterAudio {
    private final Function<String, Sound> loader;
    private final LongSupplier clock;
    private final Random random;
    private final Sound[] keys = new Sound[6];
    enum Cue {
        SPACE("space", .22f, 40_000_000L),
        BUTTON_STAMP("button_stamp", .42f, 120_000_000L),
        PAPER_OUT("paper_out", .36f, 300_000_000L);
        final String path;
        final float volume;
        final long interval;
        Cue(String name, float volume, long interval) {
            this.path = "sounds/typewriter_" + name + ".wav";
            this.volume = volume;
            this.interval = interval;
        }
    }
    private static final Cue[] CUES = Cue.values();
    private final Sound[] effects = new Sound[CUES.length];
    private final long[] lastCue = new long[CUES.length];
    private final boolean[] cuePlayed = new boolean[CUES.length];
    private long generation = -1;
    private boolean ready;
    private int previousKey = -1;
    private long lastPlayed;
    private boolean played;

    TypewriterAudio() {
        this(CustomAssetManager::getLoadedSound, System::nanoTime, new Random());
    }

    TypewriterAudio(Function<String, Sound> loader, LongSupplier clock, Random random) {
        this.loader = loader;
        this.clock = clock;
        this.random = random;
    }

    // Called before the paper opens, never from onChar. Rebind after asset reset.
    void prepare(long assetGeneration) {
        if (ready && generation == assetGeneration) return;
        generation = assetGeneration;
        ready = true;
        played = false;
        previousKey = -1;
        for (int i = 0; i < keys.length; i++) {
            keys[i] = loader.apply("sounds/typewriter_key_" + (i + 1) + ".wav");
            ready &= keys[i] != null;
        }
        for (Cue cue : CUES) {
            int index = cue.ordinal();
            effects[index] = loader.apply(cue.path);
            cuePlayed[index] = false;
            ready &= effects[index] != null;
        }
    }

    void onChar(char ch, boolean skipping) {
        if (ch == ' ') {
            playCue(Cue.SPACE, skipping);
            return;
        }
        if (!ready || skipping || Character.isWhitespace(ch) || Character.isISOControl(ch)
                || Character.isSurrogate(ch) || Character.getType(ch) == Character.PRIVATE_USE) return;
        long now = clock.getAsLong();
        // At most 25 short voices/sec (~5 overlapping), including catch-up frames.
        if (played && now - lastPlayed < 40_000_000L) return;
        played = true;
        lastPlayed = now;
        previousKey = previousKey < 0 ? random.nextInt(keys.length)
                : (previousKey + 1 + random.nextInt(keys.length - 1)) % keys.length;
        sk.sivak.eldritchhorror.core.view.utils.SoundPlayer.play(keys[previousKey], .28f + random.nextFloat() * .04f,
                .98f + random.nextFloat() * .04f, 0f, false);
    }

    void buttonsDisplayed() {
        // Choices remain visible even when the preceding text was fast-forwarded.
        playCue(Cue.BUTTON_STAMP, false);
    }

    void removePaper(boolean skipping) {
        playCue(Cue.PAPER_OUT, skipping);
    }

    private void playCue(Cue cue, boolean skipping) {
        if (!ready || skipping) return;
        int index = cue.ordinal();
        long now = clock.getAsLong();
        if (cuePlayed[index] && now - lastCue[index] < cue.interval) return;
        cuePlayed[index] = true;
        lastCue[index] = now;
        sk.sivak.eldritchhorror.core.view.utils.SoundPlayer.play(effects[index], cue.volume, .98f + random.nextFloat() * .04f, 0f, false);
    }
}
