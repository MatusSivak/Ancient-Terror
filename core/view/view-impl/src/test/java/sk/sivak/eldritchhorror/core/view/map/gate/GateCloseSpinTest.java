package sk.sivak.eldritchhorror.core.view.map.gate;

import org.junit.Test;

import static org.junit.Assert.*;

public class GateCloseSpinTest {
    private static final float TOTAL = 2.0f;

    private static float speed(float time, float peak) {
        float step = 0.001f;
        return (GateDissipateAnimation.spinDegrees(time + step, peak)
                - GateDissipateAnimation.spinDegrees(time, peak)) / step;
    }

    @Test public void spinStartsAtIdleSpeedAndAcceleratesToItsPeakAtTheEnd() {
        for (float peak : new float[]{GateDissipateAnimation.CORE_PEAK_SPIN, GateDissipateAnimation.HALO_PEAK_SPIN}) {
            assertEquals(0f, GateDissipateAnimation.spinDegrees(0f, peak), 0.001f);
            assertEquals(GateDissipateAnimation.IDLE_SPIN, speed(0f, peak), 1f);
            float previous = 0f;
            for (float t = 0f; t < TOTAL - 0.01f; t += 0.05f) {
                float current = speed(t, peak);
                assertTrue("Spin must never slow down at " + t, current >= previous - 0.01f);
                previous = current;
            }
            assertEquals(peak, speed(TOTAL - 0.002f, peak), peak * 0.01f);
            // The first half stays gentle; most of the acceleration happens near the end.
            assertTrue(speed(TOTAL / 2f, peak) < peak / 3f);
        }
    }
}
