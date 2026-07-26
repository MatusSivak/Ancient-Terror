package sk.sivak.eldritchhorror.core.view.map;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import sk.sivak.eldritchhorror.core.view.game.MapStage;

import static sk.sivak.eldritchhorror.core.constants.ViewProperties.MAP_ALLOWED_OFFSET;
import static sk.sivak.eldritchhorror.core.constants.ViewProperties.MAP_HEIGHT;

/**
 * @author msivak
 */
public class MapScrollListener extends ClickListener {

    @Override
    public boolean scrolled(InputEvent event, float x, float y, int amount) {
        float newZoom;
        OrthographicCamera camera = MapStage.getCamera();
        if (amount > 0) {
            newZoom = camera.zoom * 1.1f;
        } else {
            newZoom = camera.zoom / 1.1f;
        }
        if (newZoom * camera.viewportHeight > (MAP_HEIGHT + MAP_ALLOWED_OFFSET)) {
            newZoom = (MAP_HEIGHT + MAP_ALLOWED_OFFSET) / camera.viewportHeight;
        }
        camera.zoom = newZoom;

        MapUtils.fixInvalidCameraPosition();
        return false;
    }
}
