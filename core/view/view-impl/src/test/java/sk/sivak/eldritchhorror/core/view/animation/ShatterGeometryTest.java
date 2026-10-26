package sk.sivak.eldritchhorror.core.view.animation;

import org.junit.Test;
import static org.junit.Assert.*;

public class ShatterGeometryTest {
    @Test public void shardsCoverTheArtworkWithoutGapsOrReversedTriangles() {
        for (int[] size : new int[][]{{4, 4}, {3, 3}, {4, 2}}) {
            float area = 0;
            float[][] triangles = ShatterGeometry.triangles(size[0], size[1]);
            assertEquals(size[0] * size[1] * 2, triangles.length);
            for (float[] t : triangles) {
                float cross = (t[2] - t[0]) * (t[5] - t[1]) - (t[4] - t[0]) * (t[3] - t[1]);
                assertTrue(cross > 0);
                area += cross / 2;
                for (float coordinate : t) assertTrue(coordinate >= 0 && coordinate <= 1);
            }
            assertEquals(1f, area, .00001f);
        }
    }
}
