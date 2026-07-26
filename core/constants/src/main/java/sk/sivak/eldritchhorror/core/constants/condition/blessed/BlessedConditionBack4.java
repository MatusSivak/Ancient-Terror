package sk.sivak.eldritchhorror.core.constants.condition.blessed;

import sk.sivak.eldritchhorror.core.constants.condition.AbstractConditionBack;

public class BlessedConditionBack4 extends AbstractConditionBack {

    public BlessedConditionBack4() {
        title = "Favored";
        flavorText = "The Elder Gods hold you in high regards. " +
                "At any given moment, you feel as though you could accomplish any goal with ease.";
        effectText = "[#GOOD]You or another investigator\n" +
                "gain Boon Condition.\n" +
                "Then flip this card.[]";
    }
}
