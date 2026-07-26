package sk.sivak.eldritchhorror.core.constants.condition.hallucinations;

import sk.sivak.eldritchhorror.core.constants.condition.AbstractConditionBack;

public class HallucinationsConditionBack1 extends AbstractConditionBack {

    public HallucinationsConditionBack1() {
        title = "Delusions";
        flavorText = "You see now that all of this supposed evidence you've been collecting is just nonsense. " +
                "Your head feels so clear now! " +
                "All of the monsters and conspiracies you've been hunting were obviously figments of your imagination.";
        effectText = "[#BAD]Discard all of your Clues.[]\n" +
                "[#GOOD]Then discard this card.[]";
    }
}
