package sk.sivak.eldritchhorror.core.view.map.monster;

import org.junit.Test;
import static org.junit.Assert.*;
import static sk.sivak.eldritchhorror.core.constants.ViewProperties.*;

public class MonsterDefeatScaleTest {
    @Test public void regularEpicAndTallArtworkStayWithinDefeatArea() {
        for (float[] size : new float[][]{{108,108},{150,150},{150,300}}) {
            float scale = MonsterImageEffect.defeatScale(size[0], size[1]);
            assertTrue(scale > 0);
            assertTrue(size[0] * scale <= VIEWPORT_WIDTH * 0.3f + .01f);
            assertTrue(size[1] * scale <= VIEWPORT_HEIGHT * 0.45f + .01f);
        }
    }
}
