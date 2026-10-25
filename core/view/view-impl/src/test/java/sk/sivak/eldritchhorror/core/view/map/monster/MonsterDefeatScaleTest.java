package sk.sivak.eldritchhorror.core.view.map.monster;

import org.junit.Test;
import static org.junit.Assert.*;
import static sk.sivak.eldritchhorror.core.constants.ViewProperties.*;

public class MonsterDefeatScaleTest {
    @Test public void regularEpicAndTallArtworkStayWithinDefeatArea() {
        for (float[] size : new float[][]{{108,108},{135,135},{150,300},{300,150}}) {
            float scale = MonsterImageEffect.defeatScale(size[0], size[1]);
            assertTrue(scale > 0);
            // Include the brief recoil and epic outline when checking the screen bounds.
            assertTrue(size[0] * scale * 1.12f < VIEWPORT_WIDTH);
            assertTrue(size[1] * scale * 1.12f < VIEWPORT_HEIGHT);
        }
    }

    @Test public void ordinaryAndEpicDefeatsHaveAnObviousZoomEvenOnAZoomedMap() {
        for (float size : new float[]{108f, 135f}) {
            float scale = MonsterImageEffect.defeatScale(size, size);
            assertTrue("Portrait should fill most of the screen height", size * scale >= VIEWPORT_HEIGHT * .70f);
            assertTrue("Still grows substantially from the closest map zoom", scale / (1f / .75f) >= 2f);
        }
    }
}
