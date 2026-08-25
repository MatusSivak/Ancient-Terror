package sk.sivak.eldritchhorror.core.view.components.card;

/**
 * @author msivak
 */
public enum CardKeywords {
    LOCAL_ACTION("LOCAL ACTION:", "000cb3"),
    ACTION("ACTION:", "000cb3"),
    RECKONING("RECKONING", "9B2C2C"),
    MINUS_1("-1", "9B2C2C"),
    MINUS_2("-2", "9B2C2C"),
    MINUS_3("-3", "9B2C2C"),
    MINUS_4("-4", "9B2C2C"),
    MINUS_5("-5", "9B2C2C"),
    MINUS_6("-6", "9B2C2C"),
    PLUS_1("+1", "008000"),
    PLUS_2("+2", "008000"),
    PLUS_3("+3", "008000"),
    PLUS_4("+4", "008000"),
    PLUS_5("+5", "008000"),
    PLUS_6("+6", "008000"),
    DISCARD("Discard","9B2C2C"),
    ONCE_PER_ROUND("Once per round", "9B2C2C"),

    ARROW("→"),
    STRENGTH("Strength"),
    WILL("Will"),
    INFLUENCE("Influence"),
    OBSERVATION("Observation"),
    LORE("Lore"),
    GREEN_MINUS_TWO("greenMinus2","008000","-2");

    private String word;
    private String color;
    private String wordReplacement;

    CardKeywords(String word, String color) {
        this.word = word;
        this.color = color;
        this.wordReplacement = word;
    }

    CardKeywords(String word) {
        this.word = word;
        this.wordReplacement = word;
        this.color = "6B641Fff";
    }

    CardKeywords(String word, String color, String wordReplacement) {
        this.word = word;
        this.color = color;
        this.wordReplacement = wordReplacement;
    }

    public String getColor() {
        return color;
    }

    public String getWord() {
        return word;
    }

    public String getWordReplacement() {
        return wordReplacement;
    }
}
