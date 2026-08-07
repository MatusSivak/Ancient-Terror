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
                return new Vector2(130, 453);
            case SPACE_2:
                return new Vector2(94, 844);
            case SPACE_3:
                return new Vector2(241, 1325);
            case SPACE_4:
                return new Vector2(420, 392);
            case SPACE_5:
                return new Vector2(620, 486);
            case SPACE_6:
                return new Vector2(593, 745);
            case SPACE_7:
                return new Vector2(757, 959);
            case SPACE_8:
                return new Vector2(962, 814);
            case SPACE_9:
                return new Vector2(1050, 353);
            case SPACE_10:
                return new Vector2(1328, 905);
            case SPACE_11:
                return new Vector2(1320, 1240);
            case SPACE_12:
                return new Vector2(1387, 1496);
            case SPACE_13:
                return new Vector2(1777, 184);
            case SPACE_14:
                return new Vector2(1633, 394);
            case SPACE_15:
                return new Vector2(1587, 1322);
            case SPACE_16:
                return new Vector2(1973, 403);
            case SPACE_17:
                return new Vector2(2045, 890);
            case SPACE_18:
                return new Vector2(2105, 1334);
            case SPACE_19:
                return new Vector2(2830, 369);
            case SPACE_20:
                return new Vector2(2345, 1036);
            case SPACE_21:
                return new Vector2(2431, 1223);
            case SAN_FRANCISCO:
                return new Vector2(380, 669);
            case BUENOS_AIRES:
                return new Vector2(815, 1436);
            case ARKHAM:
                return new Vector2(842, 635);
            case LONDON:
                return new Vector2(1281, 480);
            case ROME:
                return new Vector2(1407, 715);
            case ISTANBUL:
                return new Vector2(1759, 528);
            case SHANGHAI:
                return new Vector2(2393, 737);
            case TOKYO:
                return new Vector2(2689, 642);
            case SYDNEY:
                return new Vector2(2637, 1343);
            case THE_AMAZON:
                return new Vector2(915, 1111);
            case THE_HEART_OF_AFRICA:
                return new Vector2(1628, 1005);
            case THE_PYRAMIDS:
                return new Vector2(1680, 755);
            case ANTARCTICA:
                return new Vector2(1995, 1595);
            case THE_HIMALAYAS:
                return new Vector2(2137, 702);
            case TUNGUSKA:
                return new Vector2(2253, 403);
        }
        throw new IllegalArgumentException("Invalid space: " + locationId);
    }
}
