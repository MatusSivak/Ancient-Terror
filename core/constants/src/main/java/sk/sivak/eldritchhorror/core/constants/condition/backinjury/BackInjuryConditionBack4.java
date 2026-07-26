package sk.sivak.eldritchhorror.core.constants.condition.backinjury;

import sk.sivak.eldritchhorror.core.constants.condition.AbstractConditionBack;

public class BackInjuryConditionBack4 extends AbstractConditionBack {

    public BackInjuryConditionBack4() {
        title = "Nerve Damage";
        flavorText = "The numbness in your fingers has returned, " +
                "making it nearly impossible to manipulate objects, " +
                "and you clumsily drop a valuable beyond recovery.";
        effectText = "[#BAD]Discard 1 Item possession.\n" +
                "Then flip this card.[]";
    }
}
