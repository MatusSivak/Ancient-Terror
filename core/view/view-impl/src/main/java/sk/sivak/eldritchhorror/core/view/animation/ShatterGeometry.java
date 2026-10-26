package sk.sivak.eldritchhorror.core.view.animation;

/** A jittered mesh with shared edges: every pixel belongs to exactly one shard. */
public final class ShatterGeometry {
    private ShatterGeometry() {}

    public static float[][] triangles(int columns, int rows) {
        float[][] points = new float[(columns + 1) * (rows + 1)][2];
        for (int y = 0; y <= rows; y++) {
            for (int x = 0; x <= columns; x++) {
                float[] point = points[y * (columns + 1) + x];
                point[0] = (x + (x == 0 || x == columns ? 0 : .19f * (float) Math.sin(x * 7 + y * 13))) / columns;
                point[1] = (y + (y == 0 || y == rows ? 0 : .19f * (float) Math.cos(x * 11 + y * 5))) / rows;
            }
        }
        float[][] result = new float[columns * rows * 2][];
        int index = 0;
        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < columns; x++) {
                int a = y * (columns + 1) + x, b = a + 1, d = a + columns + 1, c = d + 1;
                int[] corners = (x + y) % 2 == 0 ? new int[]{a, b, c, a, c, d} : new int[]{a, b, d, b, c, d};
                for (int t = 0; t < 2; t++) {
                    float[] triangle = result[index++] = new float[6];
                    for (int i = 0; i < 3; i++) {
                        float[] point = points[corners[t * 3 + i]];
                        triangle[i * 2] = point[0]; triangle[i * 2 + 1] = point[1];
                    }
                }
            }
        }
        return result;
    }
}
