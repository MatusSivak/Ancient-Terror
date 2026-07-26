package sk.sivak.eldritchhorror.core.constants.condition.detained;

import sk.sivak.eldritchhorror.core.constants.condition.AbstractConditionBack;

public class DetainedConditionBack1 extends AbstractConditionBack {

    public DetainedConditionBack1() {
        title = "Contaminated Water";
        flavorText = "Your captors question you for days without food or water. " +
                "The only source of water you can find in your cell comes from a rusty pipe in the wall.";

        effectText = "[#BAD]Spend 1 Clue[] or test Observation-1. If you fail,\n" +
                "[#BAD]gain a Poisoned Condition\n" +
                "and a Hallucinations Condition\n" +
                "and become Delayed.[]\n" +
                "[#GOOD]Then, discard this card.[]";
    }
}
