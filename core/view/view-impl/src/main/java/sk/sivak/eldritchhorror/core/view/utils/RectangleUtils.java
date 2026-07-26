package sk.sivak.eldritchhorror.core.view.utils;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class RectangleUtils {
    public static Vector2 randomPointInRectangle(Vector2 center, float width, float height) {
        float xPosition = MathUtils.random(width);
        float yPosition = MathUtils.random(height);

        return new Vector2(center.x - width/2 + xPosition, center.y - height/2 + yPosition);
    }
}
