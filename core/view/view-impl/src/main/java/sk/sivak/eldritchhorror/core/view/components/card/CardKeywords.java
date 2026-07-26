package sk.sivak.eldritchhorror.core.view.components.card;

/**
 * @author msivak
 */
public enum CardKeywords {
    LOCAL_ACTION("LOCAL ACTION:", "000cb3"),
    ACTION("ACTION:", "000cb3"),
    RECKONING("RECKONING", "ff0000"),
    MINUS_1("-1", "ff0000"),
    MINUS_2("-2", "ff0000"),
    MINUS_3("-3", "ff0000"),
    MINUS_4("-4", "ff0000"),
    MINUS_5("-5", "ff0000"),
    MINUS_6("-6", "ff0000"),
    PLUS_1("+1", "008000"),
    PLUS_2("+2", "008000"),
    PLUS_3("+3", "008000"),
    PLUS_4("+4", "008000"),
    PLUS_5("+5", "008000"),
    PLUS_6("+6", "008000"),

    GATE("Gate"),
    CLUE("Clue"),
    SANITY("Sanity"),
    ALLY("Ally"),
    SERVICE("Service"),
    TRINKET("Trinket"),
    STRENGTH("Strength"),
    ILLNESS("Illness"),
    MADNESS("Madness"),
    TOME("Tome"),
    TEAMWORK("Teamwork"),
    RELIC("Relic"),
    TOUGHNESS("Toughness"),
    INJURY("Injury"),
    ITEM("Item"),
    WILL("Will"),
    CURSED("Cursed"),
    DOOM("Doom"),
    CITY("City"),
    MYTHOS("Mythos"),
    OTHER_WORLD("Other World"),
    DEBT("Debt"),
    REST("Rest"),
    ARTIFACT("Artifact"),
    HEALTH("Health"),
    MAGICAL("Magical"),
    TASK("Task"),
    INFLUENCE("Influence"),
    OBSERVATION("Observation"),
    COMBAT("Combat"),
    HORROR("Horror"),
    SKILL("Skill"),
    LORE("Lore"),
    SPELL("Spell"),
    RESEARCH("Research"),
    LOST_IN_TIME_AND_SPACE("Lost in Time and Space"),
    BLESSED("Blessed"),
    DELAYED("Delayed"),
    DAMAGE("Damage"),
    MONSTER("Monster"),
    ACQUIRE_ASSETS("Acquire Assets"),
    EXPEDITION("Expedition"),
    MYSTIC_RUINS("Mystic Ruins"),
    WEAPON("Weapon"),
    UNIQUE_ASSET("Unique Asset"),
    BOON("Boon"),
    ELIXIR("Elixir"),
    INCANTATION("Incantation"),
    GLAMOUR("Glamour"),
    RITUAL("Ritual"),
    DEAL("Deal"),
    COMMON("Common"),
    BANE("Bane"),
    RESTRICTION("Restriction"),
    FOCUS("Focus");

    private String word;
    private String color;

    CardKeywords(String word, String color) {
        this.word = word;
        this.color = color;
    }

    CardKeywords(String word) {
        this.word = word;
        this.color = "000cb3ff";
    }

    public String getColor() {
        return color;
    }

    public String getWord() {
        return word;
    }
}
