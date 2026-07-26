package sk.sivak.eldritchhorror.core.view.utils;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class CircleUtils {
    public static Vector2 randomPointOnCircle(Vector2 center, float radius, boolean perimeterOnly) {
        float randomAngle = MathUtils.random(360f);
        float y = MathUtils.sinDeg(randomAngle);
        float x = MathUtils.cosDeg(randomAngle);
        if (perimeterOnly) {
            return new Vector2(center.x + x * radius, center.y + y * radius);
        } else {
            float distance = MathUtils.random(radius);
            return new Vector2(center.x + x * distance, center.y + y * distance);
        }
    }
}
