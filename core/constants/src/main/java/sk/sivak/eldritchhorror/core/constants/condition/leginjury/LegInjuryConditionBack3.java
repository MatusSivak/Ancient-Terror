package sk.sivak.eldritchhorror.core.constants.condition.leginjury;

import sk.sivak.eldritchhorror.core.constants.condition.AbstractConditionBack;

public class LegInjuryConditionBack3 extends AbstractConditionBack {

    public LegInjuryConditionBack3() {
        title = "Twisted Knee";
        flavorText = "Your knee hurts and is not strong enough to hold your weight.";
        effectText = "[#BAD]Lose 2 Health[] unless you [#BAD]become Delayed.[]\n" +
                "[#BAD]Then flip this card.[]";
    }
}
