package sk.sivak.eldritchhorror.core.view.utils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Properties;
import java.util.regex.*;
import org.junit.Test;
import static org.junit.Assert.*;
import static sk.sivak.eldritchhorror.core.view.font.FontGlyphEnricher.getGlyph;

public class LocalizedGlyphTest {
    @Test public void translatedRulesRetainTheirGlyphKeywords() throws Exception {
        Properties en = load("i18n/ui.properties"), sk = load("i18n/ui_sk.properties");
        for (String key : sk.stringPropertyNames()) {
            if (key.contains(".flavor") || key.contains(".name") || key.contains(".intro")) continue;
            if (!key.startsWith("mystery.") && !key.startsWith("rumor.card.") && !key.startsWith("investigator.special.")
                    && !key.startsWith("ancientOne.") && !key.startsWith("map.cityInfo.")) continue;
            for (String token : new String[]{"Health", "Sanity", "Clues?", "Focus", "Will", "Strength", "Observation", "Influence", "Lore", "RECKONING"})
                assertEquals(key + ":" + token, count(en.getProperty(key, ""), token), count(sk.getProperty(key), token));
        }
        String crown = MarkupText.replaceColoredGlyphKeywords(sk.getProperty("mystery.yig.CROWN_OF_THE_SERPENT.text"));
        assertTrue(crown.contains(String.valueOf(getGlyph("Will"))));
        assertTrue(MarkupText.replaceColoredGlyphKeywords(sk.getProperty("mystery.clueCost.other"))
                .contains(String.valueOf(getGlyph("Clue"))));
        assertTrue(sk.getProperty("mystery.yig.CROWN_OF_THE_SERPENT.flavor").contains("vôľa"));
    }
    private int count(String value, String token) {
        Matcher matcher = Pattern.compile("\\b" + token + "\\b").matcher(value);
        int count = 0; while (matcher.find()) count++; return count;
    }
    private Properties load(String file) throws Exception {
        Properties p = new Properties();
        try (Reader reader = new InputStreamReader(getClass().getClassLoader().getResourceAsStream(file), StandardCharsets.UTF_8)) { p.load(reader); }
        return p;
    }
}
