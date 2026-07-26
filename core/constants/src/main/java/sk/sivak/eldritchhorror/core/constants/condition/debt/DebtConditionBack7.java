package sk.sivak.eldritchhorror.core.constants.condition.debt;

import sk.sivak.eldritchhorror.core.constants.condition.AbstractConditionBack;

public class DebtConditionBack7 extends AbstractConditionBack {

    public DebtConditionBack7() {
        title = "Beyond Riches";
        flavorText = "The man wears a hat and a brown trenchcoat. " +
                "\"We do not want money,\" he hisses and grabs your throat. " +
                "You feel as if part of your identity is being stolen from you";
        effectText = "Test Will. If you fail,\n" +
                "a fragment of your soul is ripped away.\n" +
                "[#BAD]Lose 3 Sanity.[]\n" +
                "[#GOOD]Then discard this card.[]";
    }
}
