package sk.sivak.eldritchhorror.core.view.components.diceroller;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class DiceThrowMotionTest {
    @Test
    public void releaseSpreadIsBoundedAndSingleDieStartsImmediately() {
        assertEquals(0f, DiceThrowMotion.releaseDelay(0, 1), 0f);
        for (int count : new int[]{2, 10, 100}) {
            float previous = -1f;
            for (int i = 0; i < count; i++) {
                float delay = DiceThrowMotion.releaseDelay(i, count);
                assertTrue(delay > previous);
                assertTrue(delay <= 0.18001f);
                previous = delay;
            }
        }
    }

    @Test
    public void wobbleStartsAfterLastBounceAndReturnsToExactResultPose() {
        assertEquals(0f, DiceThrowMotion.wobble(0.5f), 0f);
        assertEquals(0f, DiceThrowMotion.wobble(0.90f), 0f);
        assertEquals(0f, DiceThrowMotion.wobble(1f), 0f);
        assertTrue(DiceThrowMotion.wobble(0.9125f) > 0f);
        assertTrue(DiceThrowMotion.wobble(0.9375f) < 0f);
        assertTrue(Math.abs(DiceThrowMotion.wobble(0.9625f))
                < Math.abs(DiceThrowMotion.wobble(0.9125f)));
    }
    @Test
    public void impactsStayContinuousAndEachBounceLosesHeight() {
        float previousPeak = Float.MAX_VALUE;
        float[] contacts = {0f, 0.52f, 0.76f, 0.90f};
        for (int i = 1; i < contacts.length; i++) {
            float contact = contacts[i];
            assertEquals(0f, DiceThrowMotion.height(contact), 0.0001f);
            assertEquals(DiceThrowMotion.height(contact - 0.00001f),
                    DiceThrowMotion.height(contact + 0.00001f), 0.001f);
            assertEquals(DiceThrowMotion.tumble(contact - 0.00001f),
                    DiceThrowMotion.tumble(contact + 0.00001f), 0.001f);
            float peak = DiceThrowMotion.height((contacts[i - 1] + contact) / 2f);
            assertTrue(peak > 0f && peak < previousPeak);
            previousPeak = peak;
        }
    }

    @Test
    public void throwNeverReversesAndFinishesFlatAtItsDestination() {
        float previousTravel = 0f;
        float previousTumble = 0f;
        for (int frame = 0; frame <= 1000; frame++) {
            float progress = frame / 1000f;
            float travel = DiceThrowMotion.travel(progress);
            float tumble = DiceThrowMotion.tumble(progress);
            assertTrue(travel >= previousTravel && travel <= 1f);
            assertTrue(tumble >= previousTumble && tumble <= 1f);
            assertTrue(DiceThrowMotion.height(progress) >= 0f);
            previousTravel = travel;
            previousTumble = tumble;
        }
        assertEquals(1f, DiceThrowMotion.travel(1f), 0f);
        assertEquals(1f, DiceThrowMotion.tumble(1f), 0f);
        assertEquals(0f, DiceThrowMotion.height(1f), 0f);
    }
}
