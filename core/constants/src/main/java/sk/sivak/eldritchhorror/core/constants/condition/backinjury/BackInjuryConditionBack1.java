package sk.sivak.eldritchhorror.core.constants.condition.backinjury;

import sk.sivak.eldritchhorror.core.constants.condition.AbstractConditionBack;

public class BackInjuryConditionBack1 extends AbstractConditionBack {

    public BackInjuryConditionBack1() {
        title = "Unnatural Scars";
        flavorText = "The pain in your back continues to flare up throughout the night.";
        effectText = "[#BAD]Lose 2 Health and 2 Sanity.[]\n" +
                "In the morning, you discover your back has been scarred with ancient hieroglyphs (Test Lore-1).\n" +
                "If you pass, [#GOOD]gain 1 Clue.\n" +
                "Then discard this card.[]";
    }
}
