package sk.sivak.eldritchhorror.core.constants.condition.internalinjury;

import sk.sivak.eldritchhorror.core.constants.condition.AbstractConditionBack;

public class InternalInjuryConditionBack4 extends AbstractConditionBack {

    public InternalInjuryConditionBack4() {
        title = "Fading Health";
        flavorText = "The pain in your gut continues to grow, and your fever worsens. " +
                "You know for sure now that this is no mere injury.";
        effectText = "[#BAD]Gain an Illness Condition.[]\n" +
                "[#GOOD]Then discard this card.[]";
    }
}
