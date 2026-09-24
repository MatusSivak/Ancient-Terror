package sk.sivak.eldritchhorror.core.view.components.encounter;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.utils.BaseDrawable;
import sk.sivak.eldritchhorror.core.constants.encounter.EncounterButtonData;
import sk.sivak.eldritchhorror.core.constants.encounter.EncounterType;

/** Shared visual language of the encounter chooser: accents, circular icon badge and plain skip glyph. */
final class EncounterIconStyle {

    private static final int TEXTURE_SIZE = 128;
    private static final Color BADGE_FILL = new Color(0x0e1715ff);
    private static final Color SKIP_GREY = new Color(0x9a9a96ff);
    private static final Color DISABLED_ACCENT = new Color(0x5a5448ff);
    private static final Color GOLD_ACCENT = new Color(0xbda16aff);
    private static final Color REQUIRED_ACCENT = new Color(0xf0cf7eff);
    private static final Color SKIP_ACCENT = new Color(0x7c7c78ff);

    private static Texture disc;
    private static Texture ring;
    private static TextureRegion skipGlyph;
    private static Texture glow;
    private static Texture lock;
    private static Texture cross;

    private EncounterIconStyle() {
    }

    static Color accentFor(EncounterButtonData data) {
        if (!data.isEnabled()) {
            return DISABLED_ACCENT;
        }
        return isSkip(data) ? SKIP_ACCENT : GOLD_ACCENT;
    }

    static Color requiredAccent() {
        return REQUIRED_ACCENT;
    }

    static boolean isSkip(EncounterButtonData data) {
        return data.getEncounterType() == EncounterType.SKIP
                || (data.getButtonIcon() != null && data.getButtonIcon().contains("skip"));
    }

    static BaseDrawable badge(Color accent) {
        return new BaseDrawable() {
            @Override
            public void draw(Batch batch, float x, float y, float width, float height) {
                ensureTextures();
                Color color = batch.getColor();
                float r = color.r, g = color.g, b = color.b, a = color.a;
                float size = Math.min(width, height);
                float bx = x + (width - size) / 2, by = y + (height - size) / 2;
                batch.setColor(r * BADGE_FILL.r, g * BADGE_FILL.g, b * BADGE_FILL.b, a);
                batch.draw(disc, bx, by, size, size);
                batch.setColor(r * accent.r, g * accent.g, b * accent.b, a);
                batch.draw(ring, bx, by, size, size);
                batch.setColor(r, g, b, a);
            }
        };
    }

    static TextureRegion skipGlyph() {
        ensureTextures();
        return skipGlyph;
    }

    static Texture glow() {
        ensureTextures();
        return glow;
    }

    static Texture disc() {
        ensureTextures();
        return disc;
    }

    static Texture ring() {
        ensureTextures();
        return ring;
    }

    static Texture lock() {
        ensureTextures();
        return lock;
    }

    static Texture cross() {
        ensureTextures();
        return cross;
    }

    private static void ensureTextures() {
        if (disc != null) {
            return;
        }
        disc = toTexture(circle(0f, TEXTURE_SIZE / 2f - 1));
        ring = toTexture(circle(TEXTURE_SIZE / 2f - 6, TEXTURE_SIZE / 2f - 1));
        skipGlyph = new TextureRegion(toTexture(skip()));
        glow = toTexture(radialGlow());
        lock = toTexture(supersampled(EncounterIconStyle::isLockPixel));
        cross = toTexture(supersampled(EncounterIconStyle::isCrossPixel));
    }

    private interface Shape {
        boolean contains(float x, float y);
    }

    /** White shape on a transparent square, 4x4 supersampled for smooth edges. */
    private static Pixmap supersampled(Shape shape) {
        Pixmap pixmap = new Pixmap(TEXTURE_SIZE, TEXTURE_SIZE, Pixmap.Format.RGBA8888);
        pixmap.setBlending(Pixmap.Blending.None);
        for (int py = 0; py < TEXTURE_SIZE; py++) {
            for (int px = 0; px < TEXTURE_SIZE; px++) {
                int hits = 0;
                for (int sy = 0; sy < 4; sy++) {
                    for (int sx = 0; sx < 4; sx++) {
                        if (shape.contains(px + (sx + 0.5f) / 4f, py + (sy + 0.5f) / 4f)) {
                            hits++;
                        }
                    }
                }
                pixmap.drawPixel(px, py, Color.rgba8888(1f, 1f, 1f, hits / 16f));
            }
        }
        return pixmap;
    }

    /** Padlock: rounded body with a keyhole and an arched shackle (pixmap y grows downwards). */
    private static boolean isLockPixel(float x, float y) {
        boolean body = x >= 30 && x <= 98 && y >= 58 && y <= 110
                && !(x < 38 && y < 66 && Math.hypot(x - 38, y - 66) > 8)
                && !(x > 90 && y < 66 && Math.hypot(x - 90, y - 66) > 8)
                && !(x < 38 && y > 102 && Math.hypot(x - 38, y - 102) > 8)
                && !(x > 90 && y > 102 && Math.hypot(x - 90, y - 102) > 8);
        boolean keyhole = Math.hypot(x - 64, y - 78) < 7 || (Math.abs(x - 64) < 3.5f && y >= 78 && y <= 95);
        double d = Math.hypot(x - 64, y - 42);
        boolean shackle = (y <= 42 && d >= 16 && d <= 26)
                || (y > 42 && y < 60 && ((x >= 38 && x <= 48) || (x >= 80 && x <= 90)));
        return (body && !keyhole) || shackle;
    }

    private static boolean isCrossPixel(float x, float y) {
        float u = x - 64, v = y - 64;
        boolean inside = Math.abs(u) <= 40 && Math.abs(v) <= 40;
        return inside && (Math.abs(u - v) <= 9 * 1.414f || Math.abs(u + v) <= 9 * 1.414f);
    }

    private static Pixmap radialGlow() {
        Pixmap pixmap = new Pixmap(TEXTURE_SIZE, TEXTURE_SIZE, Pixmap.Format.RGBA8888);
        pixmap.setBlending(Pixmap.Blending.None);
        float c = TEXTURE_SIZE / 2f;
        for (int py = 0; py < TEXTURE_SIZE; py++) {
            for (int px = 0; px < TEXTURE_SIZE; px++) {
                float d = (float) Math.hypot(px + 0.5f - c, py + 0.5f - c) / c;
                float alpha = clamp(1f - d);
                pixmap.drawPixel(px, py, Color.rgba8888(1f, 1f, 1f, alpha * alpha));
            }
        }
        return pixmap;
    }

    /** Antialiased white annulus; inner radius 0 gives a filled disc. */
    private static Pixmap circle(float inner, float outer) {
        Pixmap pixmap = new Pixmap(TEXTURE_SIZE, TEXTURE_SIZE, Pixmap.Format.RGBA8888);
        pixmap.setBlending(Pixmap.Blending.None);
        float c = TEXTURE_SIZE / 2f;
        for (int py = 0; py < TEXTURE_SIZE; py++) {
            for (int px = 0; px < TEXTURE_SIZE; px++) {
                float d = (float) Math.hypot(px + 0.5f - c, py + 0.5f - c);
                float alpha = Math.min(clamp(outer - d + 0.5f), inner <= 0 ? 1f : clamp(d - inner + 0.5f));
                pixmap.drawPixel(px, py, Color.rgba8888(1f, 1f, 1f, alpha));
            }
        }
        return pixmap;
    }

    /** Two triangles and an end bar, supersampled for smooth edges. */
    private static Pixmap skip() {
        Pixmap pixmap = new Pixmap(TEXTURE_SIZE, TEXTURE_SIZE, Pixmap.Format.RGBA8888);
        pixmap.setBlending(Pixmap.Blending.None);
        float top = 30, bottom = 98, mid = 64;
        for (int py = 0; py < TEXTURE_SIZE; py++) {
            for (int px = 0; px < TEXTURE_SIZE; px++) {
                int hits = 0;
                for (int sy = 0; sy < 4; sy++) {
                    for (int sx = 0; sx < 4; sx++) {
                        float x = px + (sx + 0.5f) / 4f, y = py + (sy + 0.5f) / 4f;
                        if (y < top || y > bottom) {
                            continue;
                        }
                        float halfWidth = 1f - Math.abs(y - mid) / (mid - top);
                        boolean first = x >= 20 && x <= 20 + 40 * halfWidth;
                        boolean second = x >= 56 && x <= 56 + 40 * halfWidth;
                        boolean bar = x >= 99 && x <= 111;
                        if (first || second || bar) {
                            hits++;
                        }
                    }
                }
                pixmap.drawPixel(px, py, Color.rgba8888(SKIP_GREY.r, SKIP_GREY.g, SKIP_GREY.b, hits / 16f));
            }
        }
        return pixmap;
    }

    private static Texture toTexture(Pixmap pixmap) {
        Texture texture = new Texture(pixmap);
        texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        pixmap.dispose();
        return texture;
    }

    private static float clamp(float value) {
        return Math.max(0f, Math.min(1f, value));
    }
}
