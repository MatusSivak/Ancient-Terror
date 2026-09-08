package sk.sivak.eldritchhorror.core.view.components.skilltestgrid;

import com.badlogic.gdx.audio.Sound;
import org.junit.Test;

import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static sk.sivak.eldritchhorror.core.view.components.skilltestgrid.GridTestAudio.Cue;

public class GridTestAudioTest {
    private final AtomicLong clock = new AtomicLong();
    private final Map<String, TestSound> loaded = new LinkedHashMap<>();
    private final GridTestAudio audio = new GridTestAudio(path -> {
        assertFalse("Sound must be cached: " + path, loaded.containsKey(path));
        TestSound sound = new TestSound();
        loaded.put(path, sound);
        return sound.sound;
    }, clock::get);

    @Test
    public void cuesLoadLazilyAndRepeatWithoutReloading() {
        assertTrue(loaded.isEmpty());

        audio.play(Cue.SHIFT);
        clock.set(200_000_000L);
        audio.play(Cue.SHIFT);

        assertEquals(1, loaded.size());
        assertEquals(2, sound("shift").plays);
        assertEquals(1, sound("shift").stops);
        assertEquals(0.55f, sound("shift").volume, 0f);
    }

    @Test
    public void rapidRepeatedWarningsAreThrottled() {
        audio.play(Cue.UNAVAILABLE);
        clock.set(50_000_000L);
        audio.play(Cue.UNAVAILABLE);
        assertEquals(1, sound("action_unavailable").plays);
        assertEquals(0, sound("action_unavailable").stops);

        clock.set(100_000_000L);
        audio.play(Cue.UNAVAILABLE);
        assertEquals(2, sound("action_unavailable").plays);
    }

    @Test
    public void actionReplacesSelectionButDoesNotCutOffMatchOrSpawnSounds() {
        audio.play(Cue.SELECT);
        audio.play(Cue.EXPLOSION);
        audio.play(Cue.SPAWN);
        audio.play(Cue.SWAP);

        assertEquals(1, sound("swap_select").stops);
        assertEquals(0, sound("grid_explosion").stops);
        assertEquals(0, sound("grid_spawn").stops);
        assertEquals(1, sound("swap").plays);
    }

    @Test
    public void movementCuesReplaceEachOtherWithoutUnlimitedOverlap() {
        audio.play(Cue.SHIFT);
        audio.play(Cue.REROLL);
        audio.play(Cue.PICKUP);

        assertEquals(1, sound("shift").stops);
        assertEquals(1, sound("grid_reroll").stops);
        assertEquals(0, sound("grid_pickup").stops);
    }

    @Test
    public void mixedMatchesPlayEachSoundOncePerWaveAndKeepRewardsAudible() {
        audio.playMatchWave(Arrays.asList(
                match(SymbolType.ONE), match(SymbolType.TWO),
                match(SymbolType.FIVE), match(SymbolType.SIX)
        ), 2, true);

        assertEquals(4, loaded.size());
        assertEquals(1, sound("grid_explosion").plays);
        assertEquals(1, sound("grid_implosion").plays);
        assertEquals(1, sound("grid_bonus_shift").plays);
        assertEquals(1, sound("grid_cascade").plays);
        for (TestSound sound : loaded.values()) {
            assertEquals(0, sound.stops);
        }
    }

    @Test
    public void scoringMatchWithoutBonusDoesNotPlayExplosionOrBonusSound() {
        audio.playMatchWave(Collections.singletonList(match(SymbolType.FIVE)), 0, false);

        assertEquals(1, loaded.size());
        assertEquals(1, sound("grid_implosion").plays);
    }

    @Test
    public void emptyMatchWaveIsSilent() {
        audio.playMatchWave(Collections.emptyList(), 0, true);

        assertTrue(loaded.isEmpty());
    }

    @Test
    public void stopAllClearsCooldownsWithoutDiscardingCachedAssets() {
        audio.play(Cue.SPAWN);
        audio.play(Cue.SELECT);
        audio.stopAll();

        assertEquals(1, sound("grid_spawn").stops);
        assertEquals(1, sound("swap_select").stops);

        audio.play(Cue.SPAWN);
        assertEquals(2, sound("grid_spawn").plays);
        assertEquals(2, loaded.size());
    }

    @Test
    public void everyCueHasItsOwnAssetAndIsDisposedExactlyOnce() {
        for (Cue cue : Cue.values()) {
            audio.play(cue);
        }
        assertEquals(Cue.values().length, loaded.size());
        for (String path : loaded.keySet()) {
            assertTrue(path.startsWith("sounds/"));
            assertTrue(path.endsWith(".wav"));
        }

        audio.dispose();
        audio.dispose();

        for (TestSound sound : loaded.values()) {
            assertEquals(1, sound.plays);
            assertEquals(1, sound.disposals);
            assertTrue(sound.volume > 0f && sound.volume <= 0.6f);
        }
    }

    @Test(expected = IllegalStateException.class)
    public void playbackAfterDisposalIsRejected() {
        audio.dispose();
        audio.play(Cue.SHIFT);
    }

    @Test(expected = IllegalArgumentException.class)
    public void assetLoadingErrorsAreNotSwallowed() {
        GridTestAudio brokenAudio = new GridTestAudio(path -> {
            throw new IllegalArgumentException("Missing sound: " + path);
        }, clock::get);
        brokenAudio.play(Cue.SHIFT);
    }

    private TestSound sound(String filename) {
        return loaded.get("sounds/" + filename + ".wav");
    }

    private GridMatch match(SymbolType symbol) {
        return new GridMatch(symbol, Arrays.asList(
                new GridPosition(0, 0), new GridPosition(0, 1), new GridPosition(0, 2)));
    }

    private static class TestSound {
        private int plays;
        private int stops;
        private int disposals;
        private float volume;
        private final Sound sound = (Sound) Proxy.newProxyInstance(
                Sound.class.getClassLoader(), new Class<?>[] {Sound.class}, (proxy, method, arguments) -> {
                    switch (method.getName()) {
                        case "play":
                            plays++;
                            volume = (Float) arguments[0];
                            return (long) plays;
                        case "stop":
                            stops++;
                            return null;
                        case "dispose":
                            disposals++;
                            return null;
                        default:
                            throw new UnsupportedOperationException(method.getName());
                    }
                });
    }
}
