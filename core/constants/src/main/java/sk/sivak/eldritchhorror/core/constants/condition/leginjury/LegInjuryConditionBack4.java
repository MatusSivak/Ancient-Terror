package sk.sivak.eldritchhorror.core.constants.condition.leginjury;

import sk.sivak.eldritchhorror.core.constants.condition.AbstractConditionBack;

public class LegInjuryConditionBack4 extends AbstractConditionBack {

    public LegInjuryConditionBack4() {
        title = "Stabbing Pain";
        flavorText = "You know that the injury is not severe, " +
                "but with every few steps you take, " +
                "the pain in your ankle forces you to rest for a few minutes.";
        effectText = "[#BAD]Become Delayed.\n" +
                "Then flip this card.[]";
    }
}
