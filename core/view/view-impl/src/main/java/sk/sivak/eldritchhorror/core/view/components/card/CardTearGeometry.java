package sk.sivak.eldritchhorror.core.view.components.card;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.FloatArray;

/** Shared jagged edges partition the paper exactly, without overlaps or missing strips. */
final class CardTearGeometry {
    final float[][] cracks;
    final float[][] pieces;

    CardTearGeometry(float width, float height, boolean mystery) {
        float[] edges = mystery ? new float[]{.27f, .73f, 1.53f, 2.30f, 2.77f, 3.48f}
                : new float[]{.48f, 1.53f, 2.48f, 3.50f};
        cracks = new float[edges.length][];
        pieces = new float[edges.length][];
        for (int i = 0; i < edges.length; i++) {
            Vector2 end = perimeter(edges[i], width, height);
            float dx = end.x - width / 2, dy = end.y - height / 2;
            float length = end.dst(width / 2, height / 2);
            float[] path = cracks[i] = new float[16];
            for (int j = 0; j < 8; j++) {
                float t = j / 7f;
                float jag = j == 0 || j == 7 ? 0f : ((j + i) % 2 == 0 ? 1f : -1f)
                        * Math.min(width, height) * (.012f + (j % 3) * .005f);
                path[j * 2] = width / 2 + dx * t - dy / length * jag;
                path[j * 2 + 1] = height / 2 + dy * t + dx / length * jag;
            }
        }
        for (int i = 0; i < edges.length; i++) {
            int next = (i + 1) % edges.length;
            FloatArray polygon = new FloatArray();
            polygon.addAll(cracks[i]);
            float end = next == 0 ? edges[0] + 4f : edges[next];
            for (int corner = (int) edges[i] + 1; corner < end; corner++) {
                Vector2 point = perimeter(corner, width, height);
                polygon.add(point.x); polygon.add(point.y);
            }
            float[] path = cracks[next];
            for (int j = path.length - 2; j >= 2; j -= 2) {
                polygon.add(path[j]); polygon.add(path[j + 1]);
            }
            pieces[i] = polygon.toArray();
        }
    }

    private static Vector2 perimeter(float t, float width, float height) {
        t %= 4f;
        if (t < 1f) return new Vector2(t * width, 0);
        if (t < 2f) return new Vector2(width, (t - 1f) * height);
        if (t < 3f) return new Vector2((3f - t) * width, height);
        return new Vector2(0, (4f - t) * height);
    }
}
