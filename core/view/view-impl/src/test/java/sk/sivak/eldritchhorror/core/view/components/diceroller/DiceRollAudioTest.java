package sk.sivak.eldritchhorror.core.view.components.diceroller;

import com.badlogic.gdx.audio.Sound;
import org.junit.Test;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import static org.junit.Assert.*;

public class DiceRollAudioTest {
    private final List<float[]> plays = new ArrayList<>();
    private int loads;
    private DiceRollAudio audio() {
        return new DiceRollAudio(path -> {
            loads++;
            return (Sound) Proxy.newProxyInstance(Sound.class.getClassLoader(), new Class<?>[]{Sound.class},
                    (proxy, method, args) -> {
                        if (method.getName().equals("play")) {
                            plays.add(new float[]{(float)args[0], (float)args[1], (float)args[2]});
                            return (long)plays.size();
                        }
                        fail("Playback must not stop or dispose other dice: " + method.getName());
                        return null;
                    });
        });
    }

    @Test public void tenDiceHaveIndependentOverlappingVoicesAndCachedSamples() {
        DiceRollAudio audio = audio();
        for (int i = 0; i < 10; i++) {
            DiceRollAudio.Roll roll = audio.newRoll(10);
            roll.update(0.51f, 0f);
            assertEquals(i, plays.size());
            roll.update(0.52f, -1f);
            roll.update(0.60f, 0f);
            roll.update(0.76f, 0f);
            roll.update(0.90f, 1f);
            roll.update(1f, 1f);
        }
        assertEquals(10, plays.size());
        assertEquals(3, loads);
        assertEquals(-0.65f, plays.get(0)[2], 0f);
        audio.newRoll(1).update(0.52f, 1f);
        assertEquals(0.65f, plays.get(10)[2], 0f);
        assertEquals(0.5f, plays.get(10)[0], 0f);
        assertEquals(0.5f / (float)Math.sqrt(10), plays.get(0)[0], 0.0001f);
    }

    @Test public void skippedFramesCoalesceContactsAndRerollsStartFresh() {
        DiceRollAudio audio = audio();
        DiceRollAudio.Roll roll = audio.newRoll(1);
        roll.update(1f, 0f);
        roll.update(1f, 0f);
        assertEquals(1, plays.size());
        audio.newRoll(1).update(1f, 0f);
        assertEquals(2, plays.size());
        assertEquals(0.5f, plays.get(0)[0], 0f);
    }
}
