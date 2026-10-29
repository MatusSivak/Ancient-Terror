package sk.sivak.eldritchhorror.core.view.map.clue;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TransformDrawable;

/** A clue's falling face, rim and grounded shadow share the same transform. */
public class ClueImage extends Image {
    private float spawnElevation;

    public ClueImage(Texture texture) {
        super(texture);
    }

    public void setSpawnElevation(float elevation) {
        spawnElevation = elevation;
    }

    @Override public void draw(Batch batch, float parentAlpha) {
        if (spawnElevation > 0 && getDrawable() instanceof TransformDrawable) {
            TransformDrawable drawable = (TransformDrawable) getDrawable();
            float previous = batch.getPackedColor();
            float height = MathUtils.clamp(spawnElevation / 180f, 0f, 1f);
            float shadowScale = 1f + height * .5f;
            batch.setColor(.04f, .06f, .03f, (.32f - height * .22f) * parentAlpha * getColor().a);
            drawable.draw(batch, getX(), getY() - spawnElevation, getOriginX(), getOriginY(),
                    getWidth(), getHeight(), shadowScale, shadowScale * .42f, 0);
            // Layer the rim below the tilted face so even an edge-on clue keeps its thickness.
            batch.setColor(.18f, .24f, .12f, parentAlpha * getColor().a);
            for (int edge = 3; edge > 0; edge--) {
                drawable.draw(batch, getX(), getY() - edge, getOriginX(), getOriginY(),
                        getWidth(), getHeight(), getScaleX(), getScaleY(), getRotation());
            }
            batch.setColor(previous);
        }
        super.draw(batch, parentAlpha);
    }
}
