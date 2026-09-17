package sk.sivak.eldritchhorror.core.view.components.sheet.investigator;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.utils.BaseDrawable;

import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.*;

/** Rounded geometry drawn with the existing white texture; no extra bitmap asset. */
final class RoundedSheetPanel extends BaseDrawable {
    private static final float RADIUS = 18;
    private static final int CORNER_SEGMENTS = 12;
    private final float[] vertices = new float[20];

    @Override
    public void draw(Batch batch, float x, float y, float width, float height) {
        Texture texture = getTexture(PURE_WHITE_BACKGROUND);
        float previousColor = batch.getPackedColor();
        float alpha = batch.getColor().a * 0.80f;
        batch.setColor(0.06f, 0.055f, 0.04f, alpha);
        float color = Color.toFloatBits(0.06f, 0.055f, 0.04f, alpha);
        float radius = Math.min(RADIUS, Math.min(width, height) / 2);
        batch.draw(texture, x + radius, y, width - 2 * radius, height);
        batch.draw(texture, x, y + radius, radius, height - 2 * radius);
        batch.draw(texture, x + width - radius, y + radius, radius, height - 2 * radius);
        corner(batch, texture, x + width - radius, y + height - radius, radius, 0, color);
        corner(batch, texture, x + radius, y + height - radius, radius, 90, color);
        corner(batch, texture, x + radius, y + radius, radius, 180, color);
        corner(batch, texture, x + width - radius, y + radius, radius, 270, color);
        batch.setColor(previousColor);
    }

    private void corner(Batch batch, Texture texture, float x, float y, float radius, float start, float color) {
        for (int i = 0; i < CORNER_SEGMENTS; i++) {
            float a = start + i * 90f / CORNER_SEGMENTS;
            float b = start + (i + 1) * 90f / CORNER_SEGMENTS;
            vertex(0, x, y, color);
            vertex(5, x + radius * MathUtils.cosDeg(a), y + radius * MathUtils.sinDeg(a), color);
            vertex(10, x + radius * MathUtils.cosDeg(b), y + radius * MathUtils.sinDeg(b), color);
            vertex(15, x, y, color);
            batch.draw(texture, vertices, 0, vertices.length);
        }
    }

    private void vertex(int offset, float x, float y, float color) {
        vertices[offset] = x;
        vertices[offset + 1] = y;
        vertices[offset + 2] = color;
        vertices[offset + 3] = 0.5f;
        vertices[offset + 4] = 0.5f;
    }
}
