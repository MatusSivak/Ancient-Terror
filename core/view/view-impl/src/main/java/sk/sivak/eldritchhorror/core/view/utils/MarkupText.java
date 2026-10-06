package sk.sivak.eldritchhorror.core.view.utils;

import sk.sivak.eldritchhorror.core.view.components.card.CardKeywords;
import sk.sivak.eldritchhorror.core.view.font.FontGlyphEnricher;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MarkupText {
    private static final String DICE_LIST = "[1-6](?:(?:, | or )[1-6])*";
    // Die results such as "5 or 6 →", "4, 5 or 6 →", "1 →", "On a 1 or 2:", "each 6 counts as" or "succeed on 4, 5 or 6".
    private static final Pattern DICE_RESULTS = Pattern.compile(
            "(?<![\\w+\\-])" + DICE_LIST + "(?= →)"
                    + "|(?<=\\b[Oo]n an? )" + DICE_LIST + "(?=[,:.])"
                    + "|(?<=\\bsucceed on |\\bsucceed only on )" + DICE_LIST + "(?![\\w])"
                    + "|(?<=\\b[Ee]ach )[1-6](?![\\w]| [A-Z])");
    private static final Pattern DICE_VALUE = Pattern.compile("[1-6]");

    public static String replaceDiceResults(String text) {
        return replaceDiceResults(text, "", "");
    }

    private static String replaceDiceResults(String text, String glyphPrefix, String glyphSuffix) {
        if (text == null || text.isEmpty()) {
            return text;
        }
        Matcher results = DICE_RESULTS.matcher(text);
        StringBuffer out = new StringBuffer();
        while (results.find()) {
            Matcher values = DICE_VALUE.matcher(results.group());
            StringBuffer list = new StringBuffer();
            while (values.find()) {
                char glyph = FontGlyphEnricher.getDiceGlyph(Integer.parseInt(values.group()));
                values.appendReplacement(list, Matcher.quoteReplacement(glyphPrefix + glyph + glyphSuffix));
            }
            values.appendTail(list);
            results.appendReplacement(out, Matcher.quoteReplacement(list.toString()));
        }
        results.appendTail(out);
        return out.toString();
    }

    public static String replaceGlyphKeywords(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }
        text = replaceDiceResults(text);
        for (CardKeywords cardKeywords : CardKeywords.values()) {
            String word = cardKeywords.getWord();
            if (FontGlyphEnricher.containsGlyph(word)) {
                text = text.replace(word, String.valueOf(FontGlyphEnricher.getGlyph(word)));
            }
        }
        return text;
    }

    public static String replaceImproveSkillKeywords(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }
        for (CardKeywords cardKeywords : CardKeywords.values()) {
            String word = cardKeywords.getWord();
            if (FontGlyphEnricher.containsGlyph(word) && !"→".equals(word)) {
                text = text.replace("Improve " + word, "Improve [#FFFFFFFF]" + FontGlyphEnricher.getGlyph(word) + "[]");
            }
        }
        return text;
    }

    public static String markupWithKeywords(String description, String defaultColor) {
        return markupWithKeywords(description, defaultColor, null);
    }

    public static String markupWithKeywords(String description, String defaultColor, String markupColor) {
        description = "[#" + defaultColor + "]" + replaceDiceResults(description, "[#FFFFFFFF]", "[]");
        for (CardKeywords cardKeywords : CardKeywords.values()) {
            String word = cardKeywords.getWord();

            if (FontGlyphEnricher.containsGlyph(word)) {
                description = description.replace(word, "[#FFFFFFFF]" + FontGlyphEnricher.getGlyph(word) + "[]");
                continue;
            }
            int fromIndex = 0;
            while (description.indexOf(word, fromIndex) != -1) {
                int index = description.indexOf(word, fromIndex);
                fromIndex = index + 12;
                description = description.substring(0, index) +
                        "[#" + (markupColor != null ? markupColor : cardKeywords.getColor()) + "]" +
                        cardKeywords.getWordReplacement() +
                        "[]" +
                        description.substring(index + word.length(), description.length());
            }
        }
        return description;
    }
}
