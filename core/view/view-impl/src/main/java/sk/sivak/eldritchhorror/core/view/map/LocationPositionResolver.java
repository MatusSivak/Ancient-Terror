package sk.sivak.eldritchhorror.core.view.map;

import com.badlogic.gdx.math.Vector2;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;

import static sk.sivak.eldritchhorror.core.constants.ViewProperties.MAP_HEIGHT;
import static sk.sivak.eldritchhorror.core.constants.ViewProperties.MAP_WIDTH;

/**
 * @author msivak
 */
public class LocationPositionResolver {

    public static Vector2 resolve(LocationId locationId) {
        Vector2 vector2 = resolveInternal(locationId);
        vector2.y = MAP_HEIGHT - vector2.y;
        return vector2;
    }

    public static Vector2[] resolveAll(LocationId locationId) {
        Vector2[] result = new Vector2[3];
        Vector2 vector2 = resolveInternal(locationId);
        vector2.y = MAP_HEIGHT - vector2.y;

        result[0] = new Vector2(vector2.x - MAP_WIDTH, vector2.y);
        result[1] = vector2;
        result[2] = new Vector2(vector2.x + MAP_WIDTH, vector2.y);
        return result;
    }

    private static Vector2 resolveInternal(LocationId locationId) {
        switch (locationId) {
            case SPACE_1:
                return new Vector2(208, 267);
            case SPACE_2:
                return new Vector2(182, 579);
            case SPACE_3:
                return new Vector2(429, 1080);
            case SPACE_4:
                return new Vector2(538, 220);
            case SPACE_5:
                return new Vector2(690, 374);
            case SPACE_6:
                return new Vector2(735, 498);
            case SPACE_7:
                return new Vector2(826, 672);
            case SPACE_8:
                return new Vector2(1022, 560);
            case SPACE_9:
                return new Vector2(1066, 129);
            case SPACE_10:
                return new Vector2(1357, 618);
            case SPACE_11:
                return new Vector2(1384, 1025);
            case SPACE_12:
                return new Vector2(1213, 1208);
            case SPACE_13:
                return new Vector2(1701, 75);
            case SPACE_14:
                return new Vector2(1732, 199);
            case SPACE_15:
                return new Vector2(1657, 1033);
            case SPACE_16:
                return new Vector2(2231, 180);
            case SPACE_17:
                return new Vector2(2109, 578);
            case SPACE_18:
                return new Vector2(2087, 1164);
            case SPACE_19:
                return new Vector2(2895, 245);
            case SPACE_20:
                return new Vector2(2430, 786);
            case SPACE_21:
                return new Vector2(2586, 925);
            case SAN_FRANCISCO:
                return new Vector2(380, 669);
            case BUENOS_AIRES:
                return new Vector2(919, 1346);
            case ARKHAM:
                return new Vector2(842, 635);
            case LONDON:
                return new Vector2(1428, 528);
            case ROME:
                return new Vector2(1529, 642);
            case ISTANBUL:
                return new Vector2(1878, 350);
            case SHANGHAI:
                return new Vector2(2518, 498);
            case TOKYO:
                return new Vector2(2664, 453);
            case SYDNEY:
                return new Vector2(2748, 1034);
            case THE_AMAZON:
                return new Vector2(1015, 809);
            case THE_HEART_OF_AFRICA:
                return new Vector2(1700, 741);
            case THE_PYRAMIDS:
                return new Vector2(1765, 509);
            case ANTARCTICA:
                return new Vector2(1752, 1336);
            case THE_HIMALAYAS:
                return new Vector2(2257, 436);
            case TUNGUSKA:
                return new Vector2(2344, 311);
        }
        throw new IllegalArgumentException("Invalid space: " + locationId);
    }
}
