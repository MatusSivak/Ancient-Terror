package sk.sivak.eldritchhorror.core.view.components.typewriter;

import com.badlogic.gdx.audio.Sound;
import org.junit.Test;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import static org.junit.Assert.*;

public class TypewriterAudioTest {
    private final AtomicLong clock = new AtomicLong();
    private final List<String> paths = new ArrayList<>();
    private final List<float[]> voices = new ArrayList<>();
    private int loads;
    private final TypewriterAudio audio = new TypewriterAudio(path -> {
        loads++;
        return (Sound) Proxy.newProxyInstance(Sound.class.getClassLoader(), new Class<?>[]{Sound.class},
                (proxy, method, args) -> {
                    assertEquals("play", method.getName());
                    paths.add(path);
                    voices.add(new float[]{(float) args[0], (float) args[1], (float) args[2]});
                    return 1L;
                });
    }, clock::get, new Random(917));

    @Test public void playbackNeverLoadsAssetsAndPreparationIsCached() {
        audio.prepare(0);
        assertEquals(9, loads);
        for (int i = 0; i < 1000; i++) {
            clock.addAndGet(40_000_000L);
            audio.onChar('a', false);
        }
        audio.prepare(0);
        assertEquals(9, loads);
        assertEquals(1000, voices.size());
    }

    @Test public void resetRebindsSoundsBeforeNextPaper() {
        audio.prepare(0);
        audio.prepare(1);
        assertEquals(18, loads);
        audio.onChar('a', false);
        assertEquals(1, voices.size());
    }

    @Test public void keysUseMultipleRecordingsWithoutImmediateRepeats() {
        audio.prepare(0);
        for (int i = 0; i < 60; i++) {
            clock.addAndGet(40_000_000L);
            audio.onChar('a', false);
            if (i > 0) assertNotEquals(paths.get(i - 1), paths.get(i));
        }
        assertEquals(6, new HashSet<>(paths).size());
        for (float[] voice : voices) {
            assertTrue(voice[0] >= .28f && voice[0] <= .32f);
            assertTrue(voice[1] >= .98f && voice[1] <= 1.02f);
            assertEquals(0f, voice[2], 0f);
        }
    }

    @Test public void skippingWhitespaceAndInvisibleCharactersStaySilent() {
        audio.prepare(0);
        audio.onChar('a', true);
        audio.onChar('\n', false);
        audio.onChar('\uE001', false);
        assertTrue(voices.isEmpty());
        assertEquals(9, loads);
    }

    @Test public void stalledFramesDoNotCreateABurstAndNormalTypingResumes() {
        audio.prepare(0);
        for (int i = 0; i < 100; i++) audio.onChar('a', false);
        assertEquals(1, voices.size());
        clock.addAndGet(39_000_000L);
        audio.onChar('b', false);
        assertEquals(1, voices.size());
        clock.addAndGet(1_000_000L);
        audio.onChar('b', false);
        assertEquals(2, voices.size());
    }

    @Test public void spacebarUsesItsOwnSampleWithoutReloading() {
        audio.prepare(0);
        audio.onChar(' ', false);
        assertTrue(paths.get(0).endsWith("typewriter_space.wav"));
        assertEquals(9, loads);
    }

    @Test public void buttonsStampOncePerAppearanceWithoutReloading() {
        audio.prepare(0);
        audio.buttonsDisplayed();
        assertEquals(1, paths.size());
        assertTrue(paths.get(0).endsWith("typewriter_button_stamp.wav"));
        clock.addAndGet(200_000_000L);
        audio.buttonsDisplayed();
        assertEquals(2, paths.size());
        audio.removePaper(false);
        assertTrue(paths.get(2).endsWith("typewriter_paper_out.wav"));
        assertEquals(9, loads);
    }

    @Test public void skippedActionsAreSilentAndRepeatedCuesAreCoalesced() {
        audio.prepare(0);
        audio.onChar(' ', true);
        audio.removePaper(true);
        assertTrue(paths.isEmpty());
        for (int i = 0; i < 100; i++) {
            audio.onChar(' ', false);
            audio.buttonsDisplayed();
            audio.removePaper(false);
        }
        assertEquals(3, paths.size());
        assertEquals(9, loads);
    }

    @Test public void unavailableSoundsNeverTriggerLoadingFromCharacterCallback() {
        TypewriterAudio missing = new TypewriterAudio(path -> null, clock::get, new Random(1));
        missing.onChar('a', false);
        missing.prepare(0);
        missing.onChar('a', false);
        assertTrue(voices.isEmpty());
        audio.onChar('a', false);
        assertEquals(0, loads);
    }
}
