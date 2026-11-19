package sk.sivak.eldritchhorror.core.view.map.investigator;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager;

/** A warm rotating sun or a rain cloud, floating clear of the investigator's portrait. */
final class FortuneOverlay {
    private Image light, smoke, streak;
    private final Color savedBatchColor = new Color();

    void draw(Batch batch, float parentAlpha, Actor stand, float time, boolean blessed) {
        if (light == null) {
            light = new Image(CustomAssetManager.getTexture(CustomAssetManager.LIGHT));
            smoke = new Image(CustomAssetManager.getTexture(CustomAssetManager.GATE_SMOKE));
            streak = new Image(CustomAssetManager.getTexture(CustomAssetManager.PURE_WHITE_BACKGROUND));
        }
        savedBatchColor.set(batch.getColor());
        float alpha = stand.getColor().a * parentAlpha * Math.min(1f, time / .35f);
        float x = stand.getWidth() / 2f;
        float y = stand.getHeight() + 16f + 2f * MathUtils.sin(time * 1.8f);
        if (blessed) {
            float pulse = 1f + .08f * MathUtils.sin(time * 2.4f);
            light.setColor(1f, .73f, .22f, alpha * .95f);
            drawPart(light, batch, stand, x, y, 62f * pulse, 62f * pulse, time * 16f);
            light.setColor(1f, .94f, .65f, alpha * .85f);
            drawPart(light, batch, stand, x, y, 34f, 34f, -time * 12f);
            for (int i = 0; i < 3; i++) {
                float phase = (time * .24f + i / 3f) % 1f;
                light.setColor(1f, .85f, .4f, alpha * MathUtils.sin(phase * MathUtils.PI));
                drawPart(light, batch, stand, x + (i - 1) * 16f,
                        y - 12f + phase * 30f, 12f, 12f, 0);
            }
        } else {
            // Rain stays above the face; staggered drops keep the small silhouette readable.
            for (int i = 0; i < 5; i++) {
                float fall = (time * .8f + i * .21f) % 1f;
                streak.setColor(.56f, .65f, .94f, alpha * .65f * MathUtils.sin(fall * MathUtils.PI));
                drawPart(streak, batch, stand, x - 17f + i * 8f - fall * 3f,
                        y - 2f - fall * 12f, 1.2f, 4.5f, -16f);
            }
            for (int i = 0; i < 4; i++) {
                float drift = MathUtils.sin(time * 1.3f + i * 1.6f);
                smoke.setColor(.38f + .04f * i, .32f + .04f * i, .55f + .04f * i, alpha);
                drawPart(smoke, batch, stand, x - 15f + i * 10f + drift * 2f,
                        y + (i % 2) * 5f, 30f, 24f, drift * 9f);
            }
            // Brief violet flicker within the cloud, without a full-screen flash.
            float flash = Math.max(0f, 1f - Math.abs(time % 4.8f - 3.6f) / .16f);
            light.setColor(.76f, .66f, 1f, alpha * flash * .75f);
            drawPart(light, batch, stand, x + 5f, y, 26f, 26f, 15f);
        }
        batch.setColor(savedBatchColor);
    }

    /** Follow the stand's movement, scaling, rotation and fade without adding clickable actors. */
    private void drawPart(Image image, Batch batch, Actor stand, float x, float y,
                          float width, float height, float rotation) {
        float dx = (x - stand.getOriginX()) * stand.getScaleX();
        float dy = (y - stand.getOriginY()) * stand.getScaleY();
        float cos = MathUtils.cosDeg(stand.getRotation()), sin = MathUtils.sinDeg(stand.getRotation());
        image.setBounds(stand.getX() + stand.getOriginX() + dx * cos - dy * sin - width / 2f,
                stand.getY() + stand.getOriginY() + dx * sin + dy * cos - height / 2f, width, height);
        image.setOrigin(width / 2f, height / 2f);
        image.setScale(stand.getScaleX(), stand.getScaleY());
        image.setRotation(stand.getRotation() + rotation);
        image.draw(batch, 1f);
    }
}
