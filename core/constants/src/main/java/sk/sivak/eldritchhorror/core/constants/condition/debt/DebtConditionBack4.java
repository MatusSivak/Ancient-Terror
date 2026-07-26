package sk.sivak.eldritchhorror.core.constants.condition.debt;

import sk.sivak.eldritchhorror.core.constants.condition.AbstractConditionBack;

public class DebtConditionBack4 extends AbstractConditionBack {

    public DebtConditionBack4() {
        title = "Unwilling Servant";
        flavorText = "A sorcerer holds you down and carves a symbol into your forehead. " +
                "\"Time to pay what you owe,\" he says. " +
                "You attempt to dispel the darkness that fills your thoughts.";
        effectText = "Test Lore. If you fail,\n" +
                "you have no recollection of your actions.\n" +
                "[#BAD]Gain an Amnesia Condition.[]\n" +
                "[#GOOD]Then discard this card.[]";
    }
}
