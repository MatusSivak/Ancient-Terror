package sk.sivak.eldritchhorror.core.constants.condition.debt;

import sk.sivak.eldritchhorror.core.constants.condition.AbstractConditionBack;

public class DebtConditionBack3 extends AbstractConditionBack {

    public DebtConditionBack3() {
        title = "Hitmen";
        flavorText = "You see now that it was no ordinary bank you borrowed from. " +
                "Some armed men confront you and demand that you repay what you owe.";
        effectText = "Test Strength. If you fail,\n" +
                "[#BAD]lose 3 Health.[]\n" +
                "[#GOOD]Then discard this card.[]";
    }
}
