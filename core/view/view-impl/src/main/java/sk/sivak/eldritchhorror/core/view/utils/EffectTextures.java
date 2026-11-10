package sk.sivak.eldritchhorror.core.view.utils;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.PixmapTextureData;
import com.badlogic.gdx.math.MathUtils;

/** Soft white sprites for additive effects, generated once and reloaded after a GL context loss. */
public final class EffectTextures {

    private static Texture glow, ring;

    private EffectTextures() {
    }

    /** A round glow fading smoothly from the center to the edge. */
    public static Texture glow() {
        if (glow == null) glow = radial(false);
        return glow;
    }

    /** A thin soft ring near the edge, for shockwaves. */
    public static Texture ring() {
        if (ring == null) ring = radial(true);
        return ring;
    }

    public static void dispose() {
        for (Texture texture : new Texture[]{glow, ring}) {
            if (texture == null) continue;
            texture.getTextureData().consumePixmap().dispose();
            texture.dispose();
        }
        glow = ring = null;
    }

    private static Texture radial(boolean hollow) {
        int size = 128;
        Pixmap pixmap = new Pixmap(size, size, Pixmap.Format.RGBA8888);
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                float dx = (x + .5f) / size * 2f - 1f, dy = (y + .5f) / size * 2f - 1f;
                float r = (float) Math.sqrt(dx * dx + dy * dy);
                float a;
                if (hollow) {
                    float d = (r - .86f) / .09f;
                    a = r >= 1f ? 0f : (float) Math.exp(-d * d);
                } else {
                    float f = MathUtils.clamp(1f - r, 0f, 1f);
                    a = f * f * (3f - 2f * f);
                }
                pixmap.setColor(1f, 1f, 1f, a);
                pixmap.drawPixel(x, y);
            }
        }
        // Managed data keeps the pixmap so the texture can be rebuilt when the GL context is lost.
        Texture texture = new Texture(new PixmapTextureData(pixmap, null, false, false, true));
        texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        return texture;
    }
}
