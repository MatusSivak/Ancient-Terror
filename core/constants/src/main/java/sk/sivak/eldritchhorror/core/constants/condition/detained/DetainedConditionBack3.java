package sk.sivak.eldritchhorror.core.constants.condition.detained;

import sk.sivak.eldritchhorror.core.constants.condition.AbstractConditionBack;

public class DetainedConditionBack3 extends AbstractConditionBack {

    public DetainedConditionBack3() {
        title = "Cruel and Unusual";
        flavorText = "Your captors are going to subject you to vicious tactics designed to break your will " +
                "if you do not give them the information they require.";
        effectText = "[#BAD]Spend 1 Clue[] or test Will-1. If you fail,\n" +
                "[#BAD]lose 3 Sanity\n" +
                "and gain a Paranoia Condition.[]\n" +
                "[#GOOD]Then, discard this card.[]";
    }
}
