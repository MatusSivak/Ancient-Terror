package sk.sivak.eldritchhorror.core.view.map.investigator;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager;

/** A restrained golden radiance or gathering storm, floating above the portrait. */
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
        float y = stand.getHeight() + 24f + 1.2f * MathUtils.sin(time * 1.3f);
        if (blessed) {
            float pulse = 1f + .045f * MathUtils.sin(time * 1.8f);
            // Layer the textured flare for a readable core, with no hard disc or cartoon rays.
            smoke.setColor(.28f, .21f, .11f, alpha * .65f);
            drawPart(smoke, batch, stand, x, y, 33f, 33f, -time * 5f);
            light.setColor(1f, .83f, .48f, alpha);
            drawPart(light, batch, stand, x, y, 78f * pulse, 78f * pulse, time * 7f);
            drawPart(light, batch, stand, x, y, 64f * pulse, 64f * pulse, -time * 5f);
            light.setColor(1f, .94f, .74f, alpha);
            drawPart(light, batch, stand, x, y, 40f, 40f, time * 3f);
        } else {
            // Rain stays above the face; staggered drops keep the small silhouette readable.
            for (int i = 0; i < 4; i++) {
                float fall = (time * .65f + i * .27f) % 1f;
                streak.setColor(.58f, .63f, .7f, alpha * (.35f + .4f * MathUtils.sin(fall * MathUtils.PI)));
                drawPart(streak, batch, stand, x - 16f + i * 10f - fall * 3f,
                        y - 7f - fall * 12f, 1.3f, 5f, -16f);
            }
            // Overlapping smoke keeps the body dense while retaining irregular, natural edges.
            for (int layer = 0; layer < 2; layer++) {
                smoke.setColor(.26f, .28f, .34f, alpha);
                for (int i = 0; i < 3; i++) {
                    float drift = MathUtils.sin(time * .9f + i * 1.6f);
                    drawPart(smoke, batch, stand, x - 13f + i * 13f + drift,
                            y + (i % 2) * 4f, 39f, 30f, drift * 7f + layer * 20f);
                }
            }
            for (int i = 0; i < 3; i++) {
                float drift = MathUtils.sin(time * .9f + i * 1.6f);
                smoke.setColor(.61f, .63f, .69f, alpha * .8f);
                drawPart(smoke, batch, stand, x - 13f + i * 13f + drift,
                        y + 4f + (i % 2) * 4f, 33f, 23f, drift * 7f);
            }
            // Brief cold flicker within the cloud, without a full-screen flash.
            float flash = Math.max(0f, 1f - Math.abs(time % 4.8f - 3.6f) / .16f);
            light.setColor(.74f, .79f, .9f, alpha * flash * .55f);
            drawPart(light, batch, stand, x + 5f, y, 30f, 30f, 15f);
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
