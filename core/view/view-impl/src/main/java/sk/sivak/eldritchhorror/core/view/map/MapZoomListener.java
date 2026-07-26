package sk.sivak.eldritchhorror.core.view.map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import sk.sivak.eldritchhorror.core.constants.ViewProperties;
import sk.sivak.eldritchhorror.core.view.game.MapStage;

import static sk.sivak.eldritchhorror.core.constants.ViewProperties.MAP_ALLOWED_OFFSET;
import static sk.sivak.eldritchhorror.core.constants.ViewProperties.MAP_HEIGHT;
import static sk.sivak.eldritchhorror.core.constants.ViewProperties.MAP_WIDTH;
import static sk.sivak.eldritchhorror.core.constants.ViewProperties.VIEWPORT_HEIGHT;
import static sk.sivak.eldritchhorror.core.constants.ViewProperties.VIEWPORT_WIDTH;

public class MapZoomListener extends ActorGestureListener {

    private Float previousInitialDistance = null;
    private Float initialZoom = null;
    private Float initialDistanceForCalculation = null;

    private Float initialDistance;
    private float dragStartX;
    private float dragStartY;

    @Override
    public void zoom(InputEvent event, float initialDistance, float distance) {
        if (this.initialDistance == null || this.initialDistance != initialDistance) {
            this.initialDistance = initialDistance;
            this.dragStartX = calculateX();
            this.dragStartY = calculateY();
        }

        float xMultiplier = (float) VIEWPORT_WIDTH / MapStage.getStage().getViewport().getScreenWidth();
        float yMultiplier = (float) VIEWPORT_HEIGHT / MapStage.getStage().getViewport().getScreenHeight();

        float distanceX = Math.abs(Gdx.app.getInput().getX(0) - Gdx.app.getInput().getX(1));
        distanceX *= xMultiplier;
        float distanceY = Math.abs(Gdx.app.getInput().getY(0) - Gdx.app.getInput().getY(1));
        distanceY *= yMultiplier;
        float newDistance = (float) Math.sqrt(Math.pow(distanceX,2) + Math.pow(distanceY,2));

        if (previousInitialDistance == null || !previousInitialDistance.equals(initialDistance)) {
            initialZoom = MapStage.getCamera().zoom;
            previousInitialDistance = initialDistance;
            initialDistanceForCalculation = newDistance;
        }

        float newZoom = initialZoom * (initialDistanceForCalculation/newDistance);

        if (newZoom * MapStage.getCamera().viewportHeight > (MAP_HEIGHT + MAP_ALLOWED_OFFSET)) {
            newZoom = (MAP_HEIGHT + MAP_ALLOWED_OFFSET) / MapStage.getCamera().viewportHeight;
        }
        MapStage.getCamera().zoom = newZoom;
        moveCamera();
        MapUtils.fixInvalidCameraPosition();
    }

    private float calculateX() {
        float x0 = Gdx.input.getX(0) - MapStage.getStage().getViewport().getLeftGutterWidth();
        float x1 = Gdx.input.getX(1) - MapStage.getStage().getViewport().getLeftGutterWidth();
        return (x0 + x1)/2f;
    }

    private float calculateY() {
        float y0 = -Gdx.input.getY(0) - MapStage.getStage().getViewport().getBottomGutterHeight();
        float y1 = -Gdx.input.getY(1) - MapStage.getStage().getViewport().getBottomGutterHeight();
        return (y0 + y1)/2f;
    }

    private void moveCamera() {
        float x = calculateX();
        float y = calculateY();
        OrthographicCamera camera = (OrthographicCamera) MapStage.getStage().getCamera();
        Vector3 cameraPosition = camera.position;
        float xMultiplier = (float) VIEWPORT_WIDTH / MapStage.getStage().getViewport().getScreenWidth();
        float yMultiplier = (float) VIEWPORT_HEIGHT / MapStage.getStage().getViewport().getScreenHeight();
        float newCameraX = cameraPosition.x + Math.round((dragStartX - x) * xMultiplier) * camera.zoom;
        float newCameraY = cameraPosition.y + Math.round((dragStartY - y) * yMultiplier) * camera.zoom;

        if (newCameraX < 0) {
            newCameraX += MAP_WIDTH;
        } else if (newCameraX > MAP_WIDTH) {
            newCameraX -= MAP_WIDTH;
        }

        cameraPosition.x = newCameraX;
        if (
                newCameraY - camera.viewportHeight * camera.zoom / 2 >= -MAP_ALLOWED_OFFSET &&
                        newCameraY + camera.viewportHeight * camera.zoom / 2 <= (MAP_HEIGHT + MAP_ALLOWED_OFFSET)) {
            cameraPosition.y = newCameraY;
        }
        this.dragStartX = x;
        this.dragStartY = y;
    }
}
