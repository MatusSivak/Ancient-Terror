package sk.sivak.eldritchhorror.core.view.font;

import com.badlogic.gdx.graphics.g2d.BitmapFont;

/** Bakes a base size into independent metrics while retaining the atlas UVs. */
public final class BitmapFontSizing {
    private BitmapFontSizing() { }

    public static void resize(BitmapFont font, int size) {
        if (size <= 0) throw new IllegalArgumentException("Font size must be positive");
        BitmapFont.BitmapFontData data = font.getData();
        float factor = size / 64f;
        data.setScale(factor);
        // Label.setFontScale is absolute, so the requested size must become scale 1.
        data.scaleX = 1f;
        data.scaleY = 1f;
        for (BitmapFont.Glyph[] page : data.glyphs) {
            if (page == null) continue;
            for (BitmapFont.Glyph glyph : page) {
                if (glyph == null) continue;
                glyph.width = Math.round(glyph.width * factor);
                glyph.height = Math.round(glyph.height * factor);
                glyph.xoffset = Math.round(glyph.xoffset * factor);
                glyph.yoffset = Math.round(glyph.yoffset * factor);
                glyph.xadvance = Math.round(glyph.xadvance * factor);
                if (glyph.kerning == null) continue;
                for (byte[] kernings : glyph.kerning) {
                    if (kernings == null) continue;
                    for (int i = 0; i < kernings.length; i++) {
                        kernings[i] = (byte) Math.round(kernings[i] * factor);
                    }
                }
            }
        }
    }
}
