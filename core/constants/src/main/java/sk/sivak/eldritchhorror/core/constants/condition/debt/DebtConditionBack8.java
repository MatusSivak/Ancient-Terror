package sk.sivak.eldritchhorror.core.constants.condition.debt;

import sk.sivak.eldritchhorror.core.constants.condition.AbstractConditionBack;

public class DebtConditionBack8 extends AbstractConditionBack {

    public DebtConditionBack8() {
        title = "Make a Deal";
        flavorText = "Walking into the alley, you hear the distinct ticking of a watch. " +
                "An intruder dressed in a fine black suit stands in the shadows. " +
                "He tells you that he'll help you in exchange for an undefined favor.";
        effectText = "You may gain a [#BAD]Dark Pact Condition.[]\n" +
                "If you do not, the ticking noise begins to hypnotize you,\n" +
                "[#BAD]advance Doom.[]\n" +
                "[#GOOD]Then discard this card.[]";
    }
}
