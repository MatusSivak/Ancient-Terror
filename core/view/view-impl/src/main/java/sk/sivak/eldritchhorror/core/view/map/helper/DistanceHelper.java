package sk.sivak.eldritchhorror.core.view.map.helper;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

/**
 * @author msivak
 */
public class DistanceHelper {
    public static double getDistance(Vector2 object1, Vector3 cameraPosition) {
        return Math.sqrt(Math.pow((cameraPosition.x - object1.x), 2) + Math.pow((cameraPosition.y - object1.y), 2));
    }

    public static double getDistance(Vector2 object1, Vector2 object2) {
        return Math.sqrt(Math.pow((object2.x - object1.x), 2) + Math.pow((object2.y - object1.y), 2));
    }
}
