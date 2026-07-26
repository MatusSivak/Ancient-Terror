package sk.sivak.eldritchhorror.core.constants.condition.internalinjury;

import sk.sivak.eldritchhorror.core.constants.condition.AbstractConditionBack;

public class InternalInjuryConditionBack1 extends AbstractConditionBack {

    public InternalInjuryConditionBack1() {
        title = "Internal Bleeding";
        flavorText = "The hacking fit is too excruciating to continue moving, and you begin coughing up blood.";
        effectText = "[#BAD]Lose 1 Health\n" +
                "and become Delayed.\n" +
                "[#GOOD]Then discard this card.[]";
    }
}
