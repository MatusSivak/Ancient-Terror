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
	private static final char GLYPH_RECKONING = '\uE007';
	private static final char GLYPH_HEALTH = '\uE008';
	private static final char GLYPH_SANITY = '\uE009';
	private static final char GLYPH_CLUE = '\uE00A';
	private static final char GLYPH_FOCUS = '\uE00B';
	private static final char GLYPH_FLIP = '\uE00C';
	private static final char GLYPH_DISCARD = '\uE00D';

	private static final Map<String, Character> glyphMap = new HashMap<>();
	static {
		glyphMap.put("Influence", GLYPH_INFLUENCE);
		glyphMap.put("Strength", GLYPH_STRENGTH);
		glyphMap.put("Observation", GLYPH_OBSERVATION);
		glyphMap.put("Will", GLYPH_WILL);
		glyphMap.put("Lore", GLYPH_LORE);
		glyphMap.put("→", GLYPH_ARROW);
		glyphMap.put("RECKONING", GLYPH_RECKONING);
		glyphMap.put("Health", GLYPH_HEALTH);
		glyphMap.put("Sanity", GLYPH_SANITY);
		glyphMap.put("Clues", GLYPH_CLUE);
		glyphMap.put("Clue", GLYPH_CLUE);
		glyphMap.put("Focus", GLYPH_FOCUS);
		glyphMap.put("flip this card", GLYPH_FLIP);
		glyphMap.put("Flip this card", GLYPH_FLIP);
		glyphMap.put("discard this card", GLYPH_DISCARD);
		glyphMap.put("Discard this card", GLYPH_DISCARD);

	}

	public static boolean containsGlyph(String glyphName) {
		return glyphMap.containsKey(glyphName);
	}

	public static char getGlyph(String glyphName) {
		return glyphMap.get(glyphName);
	}



	public static void enrich(BitmapFont bitmapFont) {
        enrich(bitmapFont, name -> getTextureRegion(name));
    }

    static void enrich(BitmapFont bitmapFont, java.util.function.Function<String, TextureRegion> icons) {
		// Shared fonts are requested for every label; append the icon pages only once.
		if (bitmapFont.getData().getGlyph(GLYPH_INFLUENCE) != null) return;
		int renderWidth = 90;
		int renderHeight = 90;
		FontGlyphEnricher.addIconGlyph(bitmapFont, icons.apply("glyphs/influence.png"), GLYPH_INFLUENCE, renderWidth,renderHeight);
		FontGlyphEnricher.addIconGlyph(bitmapFont, icons.apply("glyphs/strength.png"), GLYPH_STRENGTH, renderWidth,renderHeight);
		FontGlyphEnricher.addIconGlyph(bitmapFont, icons.apply("glyphs/observation.png"), GLYPH_OBSERVATION, renderWidth,renderHeight);
		FontGlyphEnricher.addIconGlyph(bitmapFont, icons.apply("glyphs/will.png"), GLYPH_WILL, renderWidth, renderHeight);
		FontGlyphEnricher.addIconGlyph(bitmapFont, icons.apply("glyphs/lore.png"), GLYPH_LORE, renderWidth,renderHeight);
		FontGlyphEnricher.addIconGlyph(bitmapFont, icons.apply("glyphs/arrow.png"), GLYPH_ARROW, 90,90);
		FontGlyphEnricher.addIconGlyph(bitmapFont, icons.apply("glyphs/reckoning.png"), GLYPH_RECKONING, renderWidth, renderHeight);
		// Health and Sanity tokens are portrait, so keep their aspect ratio.
		FontGlyphEnricher.addIconGlyph(bitmapFont, icons.apply("token/health.png"), GLYPH_HEALTH, 73, renderHeight);
		FontGlyphEnricher.addIconGlyph(bitmapFont, icons.apply("token/sanity.png"), GLYPH_SANITY, 73, renderHeight);
		FontGlyphEnricher.addIconGlyph(bitmapFont, icons.apply("token/clue.png"), GLYPH_CLUE, renderWidth, renderHeight);
		FontGlyphEnricher.addIconGlyph(bitmapFont, icons.apply("token/focus.png"), GLYPH_FOCUS, renderWidth, renderHeight);
		FontGlyphEnricher.addIconGlyph(bitmapFont, icons.apply("glyphs/flip.png"), GLYPH_FLIP, renderWidth, renderHeight);
		FontGlyphEnricher.addIconGlyph(bitmapFont, icons.apply("glyphs/discard.png"), GLYPH_DISCARD, renderWidth, renderHeight);
	}

	private static void addIconGlyph(
			BitmapFont font,
			TextureRegion icon,
			char character,
			int renderWidth,
			int renderHeight
	) {
		addIconGlyph(font, icon, character, renderWidth, renderHeight, 0);
	}

	private static void addIconGlyph(
			BitmapFont font,
			TextureRegion icon,
			char character,
			int renderWidth,
			int renderHeight,
			int yOffsetAdjustment
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
		glyph.yoffset = referenceGlyph.yoffset + (referenceGlyph.height - renderHeight) / 2 + yOffsetAdjustment;

		// Horizontal space occupied by icon.
		glyph.xadvance = renderWidth + 2;

		data.setGlyph(character, glyph);
	}
}
