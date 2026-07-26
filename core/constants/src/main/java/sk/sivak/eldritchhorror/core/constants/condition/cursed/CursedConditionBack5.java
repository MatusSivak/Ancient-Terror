package sk.sivak.eldritchhorror.core.constants.condition.cursed;

import sk.sivak.eldritchhorror.core.constants.condition.AbstractConditionBack;

public class CursedConditionBack5 extends AbstractConditionBack {

    public CursedConditionBack5() {
        title = "The Innsmouth Look";
        flavorText = "Each night, you dream of the dark comfort of the ocean's depths. " +
                "Even during the day, something beckons you to the water.";
        effectText = "If you are on a Sea space, your body begins to change horribly into a fish-like creature,\n" +
                "[#BAD]you are devoured.[]\n" +
                "If you are not on a Sea space, the change cripples you.\n" +
                "[#BAD]Lose 3 Health[]\n" +
                "and [#GOOD]discard this card.[]";
    }
}
