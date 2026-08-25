package sk.sivak.eldritchhorror.core.view.font;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.util.HashMap;
import java.util.Map;

import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.getTextureRegion;

public class FontGlyphEnricher {

	private static final char GLYPH_INFLUENCE = '\uE001';
	private static final char GLYPH_STRENGTH = '\uE002';
	private static final char GLYPH_OBSERVATION = '\uE003';
	private static final char GLYPH_WILL = '\uE004';
	private static final char GLYPH_LORE = '\uE005';
	private static final char GLYPH_ARROW = '\uE006';

	private static final Map<String, Character> glyphMap = new HashMap<>();
	static {
		glyphMap.put("Influence", GLYPH_INFLUENCE);
		glyphMap.put("Strength", GLYPH_STRENGTH);
		glyphMap.put("Observation", GLYPH_OBSERVATION);
		glyphMap.put("Will", GLYPH_WILL);
		glyphMap.put("Lore", GLYPH_LORE);
		glyphMap.put("→", GLYPH_ARROW);
	}

	public static boolean containsGlyph(String glyphName) {
		return glyphMap.containsKey(glyphName);
	}

	public static char getGlyph(String glyphName) {
		return glyphMap.get(glyphName);
	}

	public static void enrich(BitmapFont bitmapFont) {
		int renderWidth = 90;
		int renderHeight = 90;
		FontGlyphEnricher.addIconGlyph(bitmapFont, getTextureRegion("glyphs/influence.png"), GLYPH_INFLUENCE, renderWidth,renderHeight);
		FontGlyphEnricher.addIconGlyph(bitmapFont, getTextureRegion("glyphs/strength.png"), GLYPH_STRENGTH, renderWidth,renderHeight);
		FontGlyphEnricher.addIconGlyph(bitmapFont, getTextureRegion("glyphs/observation.png"), GLYPH_OBSERVATION, renderWidth,renderHeight);
		FontGlyphEnricher.addIconGlyph(bitmapFont, getTextureRegion("glyphs/will.png"), GLYPH_WILL, renderWidth,renderHeight);
		FontGlyphEnricher.addIconGlyph(bitmapFont, getTextureRegion("glyphs/lore.png"), GLYPH_LORE, renderWidth,renderHeight);
		FontGlyphEnricher.addIconGlyph(bitmapFont, getTextureRegion("glyphs/arrow.png"), GLYPH_ARROW, 90,90);
	}
	private static void addIconGlyph(
			BitmapFont font,
			TextureRegion icon,
			char character,
			int renderWidth,
			int renderHeight
	) {
		BitmapFont.BitmapFontData data = font.getData();

		// Important if icon came from a TextureAtlas:
		// make it a plain TextureRegion.
		TextureRegion region = new TextureRegion(icon);

		int page = font.getRegions().size;
		font.getRegions().add(region);

		BitmapFont.Glyph glyph = new BitmapFont.Glyph();

		glyph.id = character;
		glyph.page = page;
		glyph.srcX = 0;
		glyph.srcY = 0;

		// First use REAL source dimensions so setGlyphRegion
		// calculates UV coordinates for the entire image.
		glyph.width = region.getRegionWidth();
		glyph.height = region.getRegionHeight();

		data.setGlyphRegion(glyph, region);

		// Now change only the rendered dimensions.
		glyph.width = renderWidth;
		glyph.height = renderHeight;

		glyph.xoffset = 0;

		BitmapFont.Glyph referenceGlyph = data.getGlyph('A');
		glyph.yoffset = referenceGlyph.yoffset + (referenceGlyph.height - renderHeight) / 2;

		// Horizontal space occupied by icon.
		glyph.xadvance = renderWidth + 2;

		data.setGlyph(character, glyph);
	}
}
