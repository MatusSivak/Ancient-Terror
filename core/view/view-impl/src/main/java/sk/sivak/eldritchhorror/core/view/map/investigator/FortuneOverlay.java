package sk.sivak.eldritchhorror.core.view.map.investigator;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager;

/** A warm rotating sun or a rain cloud, floating clear of the investigator's portrait. */
final class FortuneOverlay {
    private Image light, smoke, streak;
    private Texture white;
    private final float[] discVertices = new float[20];
    private final Color savedBatchColor = new Color();

    void draw(Batch batch, float parentAlpha, Actor stand, float time, boolean blessed) {
        if (light == null) {
            light = new Image(CustomAssetManager.getTexture(CustomAssetManager.LIGHT));
            smoke = new Image(CustomAssetManager.getTexture(CustomAssetManager.GATE_SMOKE));
            white = CustomAssetManager.getTexture(CustomAssetManager.PURE_WHITE_BACKGROUND);
            streak = new Image(white);
        }
        savedBatchColor.set(batch.getColor());
        float alpha = stand.getColor().a * parentAlpha * Math.min(1f, time / .35f);
        float x = stand.getWidth() / 2f;
        float y = stand.getHeight() + 31f + 2f * MathUtils.sin(time * 1.8f);
        if (blessed) {
            float pulse = 1f + .08f * MathUtils.sin(time * 2.4f);
            light.setColor(1f, .73f, .22f, alpha);
            drawPart(light, batch, stand, x, y, 92f * pulse, 92f * pulse, time * 16f);
            // Opaque disc and rays carry the icon; the transparent flare is only decoration.
            for (int i = 0; i < 8; i++) {
                float angle = i * 45f + time * 16f;
                streak.setColor(1f, .76f, .22f, alpha);
                drawPart(streak, batch, stand, x + 23f * MathUtils.cosDeg(angle),
                        y + 23f * MathUtils.sinDeg(angle), 3.5f, 9f, angle - 90f);
            }
            batch.setColor(.45f, .23f, .035f, alpha);
            drawDisc(batch, stand, x, y, 16f, 16f);
            batch.setColor(1f, .77f, .19f, alpha);
            drawDisc(batch, stand, x, y, 14f, 14f);
            batch.setColor(1f, .94f, .56f, alpha);
            drawDisc(batch, stand, x - 2f, y + 2f, 10f, 10f);
            for (int i = 0; i < 3; i++) {
                float phase = (time * .24f + i / 3f) % 1f;
                light.setColor(1f, .85f, .4f, alpha * MathUtils.sin(phase * MathUtils.PI));
                drawPart(light, batch, stand, x + (i - 1) * 27f,
                        y - 20f + phase * 42f, 17f, 17f, 0);
            }
        } else {
            // Rain stays above the face; staggered drops keep the small silhouette readable.
            for (int i = 0; i < 5; i++) {
                float fall = (time * .8f + i * .21f) % 1f;
                streak.setColor(.69f, .78f, 1f, alpha * (.6f + .4f * MathUtils.sin(fall * MathUtils.PI)));
                drawPart(streak, batch, stand, x - 23f + i * 11f - fall * 3f,
                        y - 8f - fall * 14f, 2.4f, 7f, -16f);
            }
            // A solid, outlined cloud stays legible over both dark sea and pale map terrain.
            for (int layer = 0; layer < 2; layer++) {
                batch.setColor(layer == 0 ? .16f : .49f, layer == 0 ? .12f : .43f,
                        layer == 0 ? .28f : .69f, alpha);
                for (int i = 0; i < 4; i++) {
                    float drift = MathUtils.sin(time * 1.3f + i * 1.6f);
                    drawDisc(batch, stand, x - 19f + i * 13f + drift,
                            y + (i % 2) * 5f, 17f - layer * 2f, 13f - layer * 2f);
                }
            }
            for (int i = 0; i < 4; i++) {
                float drift = MathUtils.sin(time * 1.3f + i * 1.6f);
                smoke.setColor(.73f, .65f, .94f, alpha * .8f);
                drawPart(smoke, batch, stand, x - 19f + i * 13f + drift,
                        y + (i % 2) * 5f, 34f, 26f, drift * 9f);
            }
            // Brief violet flicker within the cloud, without a full-screen flash.
            float flash = Math.max(0f, 1f - Math.abs(time % 4.8f - 3.6f) / .16f);
            light.setColor(.76f, .66f, 1f, alpha * flash * .75f);
            drawPart(light, batch, stand, x + 5f, y, 40f, 40f, 15f);
        }
        batch.setColor(savedBatchColor);
    }

    /** Solid ellipses from the shared white texture, without new textures or per-frame allocations. */
    private void drawDisc(Batch batch, Actor stand, float x, float y, float radiusX, float radiusY) {
        float color = batch.getPackedColor();
        float cos = MathUtils.cosDeg(stand.getRotation()), sin = MathUtils.sinDeg(stand.getRotation());
        for (int segment = 0; segment < 24; segment++) {
            for (int vertex = 0; vertex < 4; vertex++) {
                float angle = (segment + (vertex == 1 ? 0 : 1)) * 360f / 24f;
                float px = x + (vertex == 0 ? 0 : radiusX * MathUtils.cosDeg(angle));
                float py = y + (vertex == 0 ? 0 : radiusY * MathUtils.sinDeg(angle));
                float dx = (px - stand.getOriginX()) * stand.getScaleX();
                float dy = (py - stand.getOriginY()) * stand.getScaleY();
                int offset = vertex * 5;
                discVertices[offset] = stand.getX() + stand.getOriginX() + dx * cos - dy * sin;
                discVertices[offset + 1] = stand.getY() + stand.getOriginY() + dx * sin + dy * cos;
                discVertices[offset + 2] = color;
                discVertices[offset + 3] = discVertices[offset + 4] = .5f;
            }
            batch.draw(white, discVertices, 0, discVertices.length);
        }
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
