package sk.sivak.eldritchhorror.core.constants.condition.poisoned;

import sk.sivak.eldritchhorror.core.constants.condition.AbstractConditionBack;

public class PoisonedConditionBack1 extends AbstractConditionBack {

    public PoisonedConditionBack1() {
        title = "Medical Assistance";
        flavorText = "The poison has spread and none of your supplies have been of any use. " +
                "You'll have to find a well-supplied medical professional in the city.";
        effectText = "If you are on a City space, test Influence-1. If you pass,\n" +
                "[#GOOD]discard this card.[]\n" +
                "If you fail, you cannot afford the antidote.\n" +
                "You may [#BAD]gain a Debt Condition[] to [#GOOD]discard this card.[]\n" +
                "[#BAD]Then flip this card.[]";
    }
}
