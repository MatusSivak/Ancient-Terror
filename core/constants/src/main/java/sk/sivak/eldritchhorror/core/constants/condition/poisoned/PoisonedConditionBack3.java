package sk.sivak.eldritchhorror.core.constants.condition.poisoned;

import sk.sivak.eldritchhorror.core.constants.condition.AbstractConditionBack;

public class PoisonedConditionBack3 extends AbstractConditionBack {

    public PoisonedConditionBack3() {
        title = "Fever Dreams";
        flavorText = "Each night the dreams become worse. You can barely sleep, and when you do, you are haunted by terrifying visions.";
        effectText = "Test Will-1. If you pass,\n" +
                "[#GOOD]discard this card.[]\n" +
                "If you fail,\n" +
                "[#BAD]gain a Hallucinations Condition\n" +
                "and flip this card.[]";
    }
}
