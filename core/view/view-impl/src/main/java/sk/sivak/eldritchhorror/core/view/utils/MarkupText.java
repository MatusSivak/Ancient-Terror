package sk.sivak.eldritchhorror.core.view.utils;

import sk.sivak.eldritchhorror.core.view.components.card.CardKeywords;

public class MarkupText {
    public static String markupWithKeywords(String description, String defaultColor) {
        return markupWithKeywords(description, defaultColor, null);
    }

    public static String markupWithKeywords(String description, String defaultColor, String markupColor) {
        description = "[#" + defaultColor + "]" + description;
        for (CardKeywords cardKeywords : CardKeywords.values()) {
            String word = cardKeywords.getWord();
            int fromIndex = 0;
            while (description.indexOf(word, fromIndex) != -1) {
                int index = description.indexOf(word, fromIndex);
                fromIndex = index + 12;
                description = description.substring(0, index) +
                        "[#" + (markupColor != null ? markupColor : cardKeywords.getColor()) + "]" +
                        description.substring(index, index + word.length()) +
                        "[]" +
                        description.substring(index + word.length(), description.length());
            }
        }
        return description;
    }
}
