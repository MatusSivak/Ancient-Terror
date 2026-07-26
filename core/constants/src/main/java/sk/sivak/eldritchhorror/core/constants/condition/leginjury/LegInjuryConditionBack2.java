package sk.sivak.eldritchhorror.core.constants.condition.leginjury;

import sk.sivak.eldritchhorror.core.constants.condition.AbstractConditionBack;

public class LegInjuryConditionBack2 extends AbstractConditionBack {

    public LegInjuryConditionBack2() {
        title = "Strange Remedy";
        flavorText = "You hop forward, trying to keep your weight on your good foot while you search for a doctor that can treat you.";
        effectText = "[#BAD]Lose 1 Health.[]\n" +
                "You may [#BAD]become Delayed[] to [#GOOD]discard this card.[]\n" +
                "If you do not discard it, [#BAD]flip this card.[]";
    }
}
