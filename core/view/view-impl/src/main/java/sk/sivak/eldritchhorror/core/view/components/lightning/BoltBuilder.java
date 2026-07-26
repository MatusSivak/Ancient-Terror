package sk.sivak.eldritchhorror.core.view.components.lightning;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class BoltBuilder {

    public static Bolt createBolt(Vector2 source, Vector2 dest, float thickness, Color color) {
        List<Line> results = new LinkedList<>();
        Vector2 tangent = new Vector2(dest).sub(source);

        // Vector2 normal = new Vector2(tangent).nor();// Vector2.Normalize(new Vector2(tangent.Y, -tangent.X));
        Vector2 normal = new Vector2(tangent.y, -tangent.x).nor();

        float length = tangent.len();

        List<Float> positions = new LinkedList<>();
        positions.add(0f);

        for (int i = 0; i < length / 20; i++)
            positions.add(new Random().nextFloat());

        Collections.sort(positions);

        float sway = 80;
        float jaggedness = 1 / sway;

        Vector2 prevPoint = source;
        float prevDisplacement = 0;
        for (int i = 1; i < positions.size(); i++) {
            float pos = positions.get(i);

            // used to prevent sharp angles by ensuring very close positions also have small perpendicular variation.
            float scale = (length * jaggedness) * (pos - positions.get(i - 1));

            // defines an envelope. Points near the middle of the bolt can be further from the central line.
            float envelope;
            float slopeStart = 0.2f;
            if (pos > slopeStart && pos < (1-slopeStart)) {
                envelope = Math.min(((1-slopeStart)-pos)*0.001f + 1, 1f + (pos - slopeStart)*0.001f);
            } else {
                envelope = 1;
            }
            //float envelope = pos > 0.33f && pos < 0.66 ? 5 * (1 - pos) : 1;

            float displacement = MathUtils.random(-sway, sway);
            displacement -= (displacement - prevDisplacement) * (1 - scale);
            displacement *= envelope;

            // Vector2 point = source + pos * tangent + displacement * normal;
            Vector2 point = new Vector2(source).add(new Vector2(tangent).scl(pos)).add(new Vector2(normal).scl(displacement));
            results.add(new Line(prevPoint, point));
            prevPoint = point;
            prevDisplacement = displacement;
        }

        results.add(new Line(prevPoint, dest));

        List<BoltSegment> boltSegments = new LinkedList<>();
        for (Line result : results) {
            boltSegments.add(new BoltSegment(result, thickness, color));
        }
        return new Bolt(boltSegments);
    }
}
