package sk.sivak.eldritchhorror.core.view.components.card;

import com.badlogic.gdx.math.EarClippingTriangulator;
import org.junit.Test;

import static org.junit.Assert.*;

public class CardTearGeometryTest {
    @Test public void jaggedPiecesCoverPortraitLandscapeAndTallCardsWithoutLosingPaper() {
        for (boolean mystery : new boolean[]{false, true}) {
            for (float[] size : new float[][]{{420, 442}, {620, 360}, {620, 780}, {200, 200}}) {
                CardTearGeometry geometry = new CardTearGeometry(size[0], size[1], mystery);
                assertEquals(mystery ? 6 : 4, geometry.pieces.length);
                float area = 0;
                for (float[] polygon : geometry.pieces) {
                    short[] triangles = new EarClippingTriangulator().computeTriangles(polygon).toArray();
                    for (int i = 0; i < triangles.length; i += 3) {
                        int a = triangles[i] * 2, b = triangles[i + 1] * 2, c = triangles[i + 2] * 2;
                        area += Math.abs((polygon[b] - polygon[a]) * (polygon[c + 1] - polygon[a + 1])
                                - (polygon[c] - polygon[a]) * (polygon[b + 1] - polygon[a + 1])) / 2;
                    }
                    for (int i = 0; i < polygon.length; i += 2) {
                        assertTrue(polygon[i] >= 0 && polygon[i] <= size[0]);
                        assertTrue(polygon[i + 1] >= 0 && polygon[i + 1] <= size[1]);
                    }
                }
                assertEquals(size[0] * size[1], area, size[0] * size[1] * .00001f);
            }
        }
    }
}
