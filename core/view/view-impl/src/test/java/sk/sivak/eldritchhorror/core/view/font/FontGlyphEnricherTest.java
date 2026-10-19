package sk.sivak.eldritchhorror.core.view.font;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.TextureData;
import com.badlogic.gdx.graphics.Pixmap;
import org.junit.Test;
import static org.junit.Assert.*;

public class FontGlyphEnricherTest {
    @Test public void repeatedLabelFontRequestsDoNotAccumulatePages() {
        BitmapFont.BitmapFontData data = new BitmapFont.BitmapFontData() {
            @Override public void setGlyphRegion(BitmapFont.Glyph glyph, TextureRegion region) {
                // No GPU texture is needed to check page and glyph registration.
            }
        };
        data.setGlyph('A', new BitmapFont.Glyph());
        BitmapFont font = new BitmapFont(data, new TextureRegion(), false) {
            @Override protected void load(BitmapFontData ignored) { }
        };
        GL20 previousGl = Gdx.gl;
        Gdx.gl = (GL20) java.lang.reflect.Proxy.newProxyInstance(GL20.class.getClassLoader(),
                new Class[]{GL20.class}, (proxy, method, args) ->
                        method.getReturnType() == int.class ? 1 : null);
        try {
        Texture texture = new Texture(new TextureData() {
            public TextureDataType getType() { return TextureDataType.Custom; }
            public boolean isPrepared() { return true; }
            public void prepare() { }
            public Pixmap consumePixmap() { throw new UnsupportedOperationException(); }
            public boolean disposePixmap() { return false; }
            public void consumeCustomData(int target) { }
            public int getWidth() { return 90; }
            public int getHeight() { return 90; }
            public Pixmap.Format getFormat() { return Pixmap.Format.RGBA8888; }
            public boolean useMipMaps() { return false; }
            public boolean isManaged() { return false; }
        });
        int[] loads = {0};
        for (int i = 0; i < 1000; i++) {
            FontGlyphEnricher.enrich(font, path -> {
                loads[0]++;
                return new TextureRegion(texture);
            });
        }
        assertEquals(16, loads[0]);
        assertEquals(22, font.getRegions().size);
        for (String ticket : new String[]{"Train Ticket", "Ship Ticket"}) {
            BitmapFont.Glyph glyph = data.getGlyph(FontGlyphEnricher.getGlyph(ticket));
            assertEquals(64, glyph.width);
            assertEquals(90, glyph.height);
            TextureRegion cropped = font.getRegions().get(glyph.page);
            assertTrue(cropped.getRegionWidth() < texture.getWidth() / 2);
            assertTrue(cropped.getRegionHeight() < texture.getHeight());
        }
        for (int value = 1; value <= 6; value++) {
            assertNotNull(data.getGlyph(FontGlyphEnricher.getDiceGlyph(value)));
        }
        for (String name : new String[]{"Influence", "Strength", "Observation", "Will", "Lore", "→", "RECKONING",
                "Health", "Sanity", "Clue", "Clues", "Focus", "flip this card", "discard this card", "Train Ticket", "Ship Ticket"}) {
            assertNotNull(data.getGlyph(FontGlyphEnricher.getGlyph(name)));
        }
        } finally {
            Gdx.gl = previousGl;
        }
    }
}
