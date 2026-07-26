package sk.sivak.eldritchhorror.core.view.map.helper;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.FloatAction;
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction;
import rx.CompletableSubscriber;
import sk.sivak.eldritchhorror.core.constants.ViewProperties;
import sk.sivak.eldritchhorror.core.view.game.MapStage;
import sk.sivak.eldritchhorror.core.view.map.CameraActor;
import sk.sivak.eldritchhorror.core.view.utils.FastForwardAction;

import static sk.sivak.eldritchhorror.core.constants.ViewProperties.*;
import static sk.sivak.eldritchhorror.core.view.map.helper.DistanceHelper.getDistance;

/**
 * @author msivak
 */
public class MoveCameraToLocationHelper {


    private static boolean enabled = true;

    public static void moveCameraToPosition(Vector2 position,
                                            CompletableSubscriber subscriber) {
        moveCameraToPosition(position, subscriber, ZOOMED_MIN_VIEWPORT_HEIGHT / MapStage.getCamera().viewportHeight);
    }

    public static void moveCameraToPosition(Vector2 position,
                                            CompletableSubscriber subscriber,
                                            float targetZoom) {
        moveCameraToPosition(position, subscriber, targetZoom, null);
    }

    public static void moveCameraToPosition(Vector2 position,
                                            CompletableSubscriber subscriber,
                                            float targetZoom, Float duration) {

        if (!enabled) {
            subscriber.onCompleted();
            return;
        }
        OrthographicCamera camera = MapStage.getCamera();
        Vector2 locationPositionLeft = new Vector2(position.x - MAP_WIDTH, position.y);
        Vector2 locationPositionRight = new Vector2(position.x + MAP_WIDTH, position.y);

        float distanceCenter = (float) getDistance(position, camera.position);
        float distanceLeft = (float) getDistance(locationPositionLeft, camera.position);
        float distanceRight = (float) getDistance(locationPositionRight, camera.position);

        MoveToAction moveToAction;
        if (distanceLeft < distanceCenter) {
            moveToAction = createMoveToAction(locationPositionLeft, distanceLeft / CAMERA_MOVE_SPEED);
        } else if (distanceRight < distanceCenter) {
            moveToAction = createMoveToAction(locationPositionRight, distanceRight / CAMERA_MOVE_SPEED);
        } else {
            moveToAction = createMoveToAction(position, distanceCenter / CAMERA_MOVE_SPEED);
        }

        float zoomDifference = Math.abs(camera.zoom - targetZoom);
        zoomDifference /= VIEWPORT_HEIGHT / camera.viewportHeight;
        float zoomDuration = zoomDifference / CAMERA_ZOOM_SPEED;

        CameraActor cameraActor = new CameraActor(camera);
        cameraActor.setPosition(camera.position.x, camera.position.y);
        MapStage.getStage().addActor(cameraActor);

        if (duration != null) {
            zoomDuration = duration;
            moveToAction.setDuration(duration);
        }
        if (zoomDuration > moveToAction.getDuration()) {
            moveToAction.setDuration(zoomDuration);
            ZoomAction zoomAction = new ZoomAction(cameraActor, camera.zoom, targetZoom, zoomDuration);

            cameraActor.addAction(
                    Actions.sequence(
                            new FastForwardAction(Actions.parallel(zoomAction, moveToAction)),
                            Actions.run(() -> {subscriber.onCompleted();}),
                            Actions.run(cameraActor::remove)));
        } else {
            float zoomOutDuration = (moveToAction.getDuration() - zoomDuration) / 2;
            float zoomInDuration = moveToAction.getDuration() - zoomOutDuration;
            float zoomOutEnd = camera.zoom + zoomOutDuration * CAMERA_ZOOM_SPEED;
            ZoomAction zoomOutAction = new ZoomAction(cameraActor, camera.zoom, zoomOutEnd, zoomOutDuration);
            ZoomAction zoomInAction = new ZoomAction(cameraActor, zoomOutEnd, targetZoom, zoomInDuration);
            cameraActor.addAction(
                    Actions.sequence(
                            new FastForwardAction(Actions.parallel(Actions.sequence(zoomOutAction, zoomInAction),moveToAction)),
                            Actions.run(() -> {subscriber.onCompleted();}),
                            Actions.run(cameraActor::remove)));
        }
    }

    private static MoveToAction createMoveToAction(Vector2 locationPosition, float distance) {
        MoveToAction moveToAction = new MoveToAction();
        moveToAction.setPosition(locationPosition.x, locationPosition.y);
        moveToAction.setDuration((float) Math.sqrt(distance));
        moveToAction.setInterpolation(Interpolation.sine);
        return moveToAction;
    }

    private static class ZoomAction extends FloatAction {

        private final float start;
        private final float end;
        private CameraActor cameraActor;

        public ZoomAction(CameraActor cameraActor, float start, float end, float duration) {
            super(start, end);
            this.start = start;
            this.end = end;
            setDuration(duration);
            this.cameraActor = cameraActor;
            setInterpolation(Interpolation.sine);
        }

        @Override
        protected void end() {
            cameraActor.setActive(this, false);
            MapStage.getCamera().zoom = end;
        }

        @Override
        protected void begin() {
            cameraActor.setActive(this, true);
            MapStage.getCamera().zoom = start;
        }
    }

    public static void setEnabled(boolean enabled) {
        MoveCameraToLocationHelper.enabled = enabled;
    }
}
