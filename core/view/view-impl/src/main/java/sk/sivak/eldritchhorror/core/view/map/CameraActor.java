package sk.sivak.eldritchhorror.core.view.map;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.FloatAction;

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

import static sk.sivak.eldritchhorror.core.constants.ViewProperties.MAP_WIDTH;

/**
 * @author msivak
 */
public class CameraActor extends Actor {
    private OrthographicCamera camera;

    private Map<FloatAction, Boolean> zoomActionsMap = new HashMap<>();
    private final List<Runnable> completionListeners = new ArrayList<>();
    private boolean completed;

    public CameraActor(OrthographicCamera camera) {
        this.camera = camera;
    }

    @Override
    public void act(float delta) {
        if (completed) return;
        super.act(delta);

        for (Map.Entry<FloatAction, Boolean> entry : zoomActionsMap.entrySet()) {
            if (!entry.getValue()) {
                continue;
            }
            camera.zoom = entry.getKey().getValue();
        }
        camera.position.x = getX();
        camera.position.y = getY();

        if (camera.position.x < 0) {
            camera.position.x += MAP_WIDTH;
        } else if (camera.position.x > MAP_WIDTH) {
            camera.position.x -= MAP_WIDTH;
        }

        // Publish the final camera position and detach before callbacks can start
        // the next move. This actor must never overwrite that move afterward.
        if (!hasActions()) {
            completed = true;
            remove();
            for (Runnable listener : completionListeners) listener.run();
            completionListeners.clear();
        }
    }

    public void afterMovement(Runnable listener) {
        if (completed) listener.run();
        else completionListeners.add(listener);
    }

    public void setActive(FloatAction floatAction, boolean active) {
        zoomActionsMap.put(floatAction, active);
    }
}
