package sk.sivak.eldritchhorror.core.constants.condition.poisoned;

import sk.sivak.eldritchhorror.core.constants.condition.AbstractConditionBack;

public class PoisonedConditionBack2 extends AbstractConditionBack {

    public PoisonedConditionBack2() {
        title = "Persisting Illness";
        flavorText = "The rash covering most of your body continues to spread. " +
                "You are regularly stricken with fits of coughing, and it has become hard to move.";
        effectText = "Test Strength. If you pass,\n" +
                "[#GOOD]discard this card.[]\n" +
                "If you fail, those around you fear the illness to be contagious.\n" +
                "[#BAD]discard 1 Ally Asset\n" +
                "and flip this card.[]";
    }
}
