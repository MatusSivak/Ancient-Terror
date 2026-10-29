package sk.sivak.eldritchhorror.core.view.map.expedition;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.*;

/** A compass that reveals its destination with a brief cartographic sweep. */
public final class ExpeditionImage extends Image {
    private float spawnProgress = 1f;

    public ExpeditionImage(Texture texture) {
        super(texture);
    }

    public void setSpawnProgress(float progress) {
        spawnProgress = progress;
    }

    public void startIdleAnimation() {
        addAction(Actions.forever(Actions.sequence(
                Actions.rotateTo(-15, 1f, Interpolation.sine),
                Actions.rotateTo(15, 1f, Interpolation.sine))));
    }

    @Override public void draw(Batch batch, float parentAlpha) {
        if (spawnProgress > 0 && spawnProgress < 1) {
            float previous = batch.getPackedColor();
            float fade = Math.min(1, spawnProgress / .15f) * (1 - Interpolation.sineIn.apply(spawnProgress));
            batch.setColor(.95f, .74f, .32f, .85f * fade * parentAlpha);
            Texture white = getTexture(PURE_WHITE_BACKGROUND);
            float radius = getWidth() * (.5f + .18f * spawnProgress);
            float cx = getX() + getOriginX(), cy = getY() + getOriginY();
            // Draw a growing elliptical ring directly on the map, behind the compass.
            int segments = Math.min(64, (int) (spawnProgress * 128));
            for (int i = 0; i < segments; i++) {
                float angle = i * 360f / 64f;
                float next = (i + 1) * 360f / 64f;
                float x = cx + MathUtils.cosDeg(angle) * radius;
                float y = cy + MathUtils.sinDeg(angle) * radius * .65f;
                float dx = (MathUtils.cosDeg(next) - MathUtils.cosDeg(angle)) * radius;
                float dy = (MathUtils.sinDeg(next) - MathUtils.sinDeg(angle)) * radius * .65f;
                batch.draw(white, x, y, 0, .6f, (float) Math.sqrt(dx * dx + dy * dy) + .5f, 1.2f,
                        1, 1, MathUtils.atan2(dy, dx) * MathUtils.radiansToDegrees,
                        0, 0, white.getWidth(), white.getHeight(), false, false);
            }
            for (int i = 0; i < 8; i++) {
                float angle = i * 45f;
                float x = cx + MathUtils.cosDeg(angle) * radius;
                float y = cy + MathUtils.sinDeg(angle) * radius * .65f;
                batch.draw(white, x - 4, y - .6f, 4, .6f, 8, 1.2f,
                        1, 1, angle, 0, 0, white.getWidth(), white.getHeight(), false, false);
            }
            batch.setColor(previous);
        }
        super.draw(batch, parentAlpha);
    }
}
