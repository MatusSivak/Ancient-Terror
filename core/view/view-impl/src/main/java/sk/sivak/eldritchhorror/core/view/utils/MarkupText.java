package sk.sivak.eldritchhorror.core.view.utils;

import sk.sivak.eldritchhorror.core.view.components.card.CardKeywords;
import sk.sivak.eldritchhorror.core.view.font.FontGlyphEnricher;

public class MarkupText {
    public static String markupWithKeywords(String description, String defaultColor) {
        return markupWithKeywords(description, defaultColor, null);
    }

    public static String markupWithKeywords(String description, String defaultColor, String markupColor) {
        description = "[#" + defaultColor + "]" + description;
        for (CardKeywords cardKeywords : CardKeywords.values()) {
            String word = cardKeywords.getWord();

            if (FontGlyphEnricher.containsGlyph(word)) {
                description = description.replaceAll(word, "[#FFFFFFFF]" + FontGlyphEnricher.getGlyph(word) + "[]");
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
