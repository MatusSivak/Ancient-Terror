package sk.sivak.eldritchhorror.core.constants.condition.debt;

import sk.sivak.eldritchhorror.core.constants.condition.AbstractConditionBack;

public class DebtConditionBack1 extends AbstractConditionBack {

    public DebtConditionBack1() {
        title = "Bag Man";
        flavorText = "A man surprises you in your hotel room and presses you for information. " +
                "You fear that if you do not tell him what he wants to know, you'll never be heard from again.";

        effectText = "You may [#BAD]spend 1 Clue.[]\n" +
                "If you do not spend the Clue,\n" +
                "[#BAD]move to a random space and become Delayed.[]\n" +
                "[#GOOD]Then discard this card.[]";
    }
}
