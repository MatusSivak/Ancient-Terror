package sk.sivak.eldritchhorror.core.constants.condition.debt;

import sk.sivak.eldritchhorror.core.constants.condition.AbstractConditionBack;

public class DebtConditionBack2 extends AbstractConditionBack {

    public DebtConditionBack2() {
        title = "Poisoned Meal";
        flavorText = "Your creditors seem almost forgiving when you tell them you cannot pay your dues. " +
                "They beg you not to worry, but their kind expressions change as you eat the meal they've offered you.";
        effectText = "[#BAD]Gain a Poisoned Condition.[]\n" +
                "[#GOOD]Then discard this card.[]";
    }
}
