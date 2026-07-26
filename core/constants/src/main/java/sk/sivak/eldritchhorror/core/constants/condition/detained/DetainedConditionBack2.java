package sk.sivak.eldritchhorror.core.constants.condition.detained;

import sk.sivak.eldritchhorror.core.constants.condition.AbstractConditionBack;

public class DetainedConditionBack2 extends AbstractConditionBack {

    public DetainedConditionBack2() {
        title = "Holding Cell";
        flavorText = "Your captors rarely speak, except to interrogate you for information. " +
                "You don't think you can hold out much longer, " +
                "and you hope that the guard that watches your cell might take pity on you.";
        effectText = "[#BAD]Spend 1 Clue[] or test Influence-1. If you fail,\n" +
                "they beat you and leave you to die.\n" +
                "[#BAD]Lose 2 Health and 2 Sanity.[]\n" +
                "[#GOOD]Then, discard this card.[]";
    }
}
