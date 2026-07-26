package sk.sivak.eldritchhorror.core.constants.condition.amnesia;

import sk.sivak.eldritchhorror.core.constants.condition.AbstractConditionBack;

public class AmnesiaConditionBack4 extends AbstractConditionBack {

    public AmnesiaConditionBack4() {
        title = "Forbidden Words";
        flavorText = "So much misfortune cannot be the side effect of simple bad luck. " +
                "Your recent string of failures must be due to a hex of some kind. " +
                "The markings you discover on your body confirms your suspicions. " +
                "At some point, you spoke the forbidden words.";
        effectText = "[#BAD]Gain a Cursed Condition.[]\n" +
                "[#GOOD]Then discard this card.[]";
    }
}
