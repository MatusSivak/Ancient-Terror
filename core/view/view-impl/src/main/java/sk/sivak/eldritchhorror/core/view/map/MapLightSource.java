package sk.sivak.eldritchhorror.core.view.map;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;

/** A transient effect in the sky layer that lights the terrain below it (see {@link SingleMap}). */
public interface MapLightSource {

    /** Light strength; 0 or less switches the light off. */
    float getMapLightStrength();

    float getMapLightRadius();

    Color getMapLightColor();

    /** Fills {@code out} with the light center in map (stage) coordinates. */
    Vector2 getMapLightCenter(Vector2 out);
}
