package sk.sivak.eldritchhorror.core.view.font;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import org.junit.Test;
import java.io.File;
import static org.junit.Assert.*;

public class BitmapFontSizingTest {
    @Test public void labelScalingPreservesTheRequestedBaseSize() {
        BitmapFont font = font("Source Serif 4");
        float originalCapHeight = font.getCapHeight();
        float originalWidth = new GlyphLayout(font, "Solved mysteries").width;
        BitmapFontSizing.resize(font, 40);
        assertEquals(1f, font.getScaleX(), 0f);
        assertEquals(originalCapHeight * 40f / 64, font.getCapHeight(), 0.001f);
        assertEquals(originalWidth * 40f / 64, new GlyphLayout(font, "Solved mysteries").width, 5f);
        float compactWidth = new GlyphLayout(font, "Solved mysteries").width;
        font.getData().setScale(0.5f);
        assertEquals(compactWidth / 2, new GlyphLayout(font, "Solved mysteries").width, 0.01f);
        font.getData().setScale(1f);
        assertEquals(originalCapHeight * 40f / 64, font.getCapHeight(), 0.001f);
        assertEquals(originalCapHeight, font("Source Serif 4").getCapHeight(), 0f);
    }

    @Test public void bundledFontsContainSlovakCharactersAndAllAtlasPages() {
        for (String name : new String[]{"Source Serif 4", "Special_Elite", "Libre Baskerville", "Cinzel"}) {
            BitmapFont font = font(name);
            for (char character : "ÁÄČĎÉÍĹĽŇÓÔŔŠŤÚÝŽáäčďéíĺľňóôŕšťúýž".toCharArray()) {
                assertTrue(name + " missing " + character, font.getData().hasGlyph(character));
            }
            for (String path : font.getData().imagePaths) assertTrue(path, new File(path).isFile());
        }
    }

    private static BitmapFont font(String name) {
        File root = new File(System.getProperty("user.dir"));
        while (root != null && !new File(root, "assets/new_font").isDirectory()) root = root.getParentFile();
        assertNotNull(root);
        BitmapFont.BitmapFontData data = new BitmapFont.BitmapFontData(
                new FileHandle(new File(root, "assets/new_font/" + name + "/hiero.fnt")), false);
        return new BitmapFont(data, new TextureRegion(), false) {
            @Override protected void load(BitmapFontData data) { }
        };
    }
}
