package sk.sivak.eldritchhorror.core.constants.condition.debt;

import sk.sivak.eldritchhorror.core.constants.condition.AbstractConditionBack;

public class DebtConditionBack5 extends AbstractConditionBack {

    public DebtConditionBack5() {
        title = "Local Authorities";
        flavorText = "\"Someone was asking about you,\" says a fellow traveler. " +
                "\"They said you owe quite a bit of money. " +
                "Naturally, I said that I didn't know anything, but you may want to find your way to a new town soon.\" " +
                "You attempt to hide from the police.";
        effectText = "Test Observation. If you fail,\n" +
                "move to the nearest City space\n" +
                "[#BAD]and gain a Detained Condition[]\n" +
                "unless you [#BAD]discard 1 Ally.[]\n" +
                "[#GOOD]Then discard this card.[]";
    }
}
