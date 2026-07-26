package sk.sivak.eldritchhorror.core.constants.condition.internalinjury;

import sk.sivak.eldritchhorror.core.constants.condition.AbstractConditionBack;

public class InternalInjuryConditionBack2 extends AbstractConditionBack {

    public InternalInjuryConditionBack2() {
        title = "Cracked Rib";
        flavorText = "You keep your hand pressed against your side to minimize the pain, but each breath is still agonizing.";
        effectText = "[#BAD]Lose 1 Health.\n" +
                "Then flip this card.[]";
    }
}
