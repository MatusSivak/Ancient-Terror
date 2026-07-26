package sk.sivak.eldritchhorror.core.view.map;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import rx.Completable;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.view.game.MapStage;
import sk.sivak.eldritchhorror.core.view.map.helper.MoveCameraToLocationHelper;

import static sk.sivak.eldritchhorror.core.constants.ViewProperties.*;

/**
 * @author msivak
 */
public class MapUtils {

    private static boolean cameraPositionFixEnabled = true;

    public static void disableCameraPositionFix() {
        MapUtils.cameraPositionFixEnabled = false;
    }

    public static void enableCameraPositionFix() {
        MapUtils.cameraPositionFixEnabled = true;
    }

    static void fixInvalidCameraPosition() {
        if (!cameraPositionFixEnabled) {
            return;
        }
        OrthographicCamera camera = MapStage.getCamera();
        if (camera.position.y + (camera.zoom * camera.viewportHeight / 2) > MAP_HEIGHT + MAP_ALLOWED_OFFSET) {
            camera.position.y = MAP_HEIGHT + MAP_ALLOWED_OFFSET - (camera.zoom * camera.viewportHeight / 2);
        } else if (camera.position.y - (camera.zoom * camera.viewportHeight / 2) < -MAP_ALLOWED_OFFSET) {
            camera.position.y = (camera.zoom * camera.viewportHeight / 2) - MAP_ALLOWED_OFFSET;
        }
    }

    public static void resetCamera() {
        MapStage.getCamera().zoom = MAP_WIDTH / MapStage.getCamera().viewportWidth;
        MapStage.getCamera().position.y = MAP_HEIGHT / 2;
        MapStage.getCamera().position.x = MAP_WIDTH / 2;
    }

    public static Completable showWholeWorld() {
        return Completable.create(onSub -> {
            MoveCameraToLocationHelper.moveCameraToPosition(new Vector2(MAP_WIDTH / 2f, MAP_HEIGHT / 2f),
                    onSub,
                    MAP_WIDTH / MapStage.getCamera().viewportWidth, NORMAL_ACTION_DURATION);
        });
    }

    public static Completable moveCameraToLocation(LocationId locationId) {
        return Completable.create(onSub -> {
            MoveCameraToLocationHelper.moveCameraToPosition(LocationPositionResolver.resolve(locationId), onSub);
        });
    }

}
