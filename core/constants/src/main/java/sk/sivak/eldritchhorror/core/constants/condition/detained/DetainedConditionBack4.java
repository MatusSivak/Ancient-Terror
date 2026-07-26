package sk.sivak.eldritchhorror.core.constants.condition.detained;

import sk.sivak.eldritchhorror.core.constants.condition.AbstractConditionBack;

public class DetainedConditionBack4 extends AbstractConditionBack {

    public DetainedConditionBack4() {
        title = "Starvation";
        flavorText = "\"The food is poison\" a fellow prisoner whispers, \"Don't eat it.\" " +
                "You will surely starve to death if you do not give your captors the information they're seeking.";
        effectText = "[#BAD]Spend 1 Clue[] or test Strength-1. If you fail,\n" +
                "[#BAD]lose 3 Health\n" +
                "and gain an Internal Injury Condition.[]\n" +
                "[#GOOD]Then, discard this card.[]";
    }
}
