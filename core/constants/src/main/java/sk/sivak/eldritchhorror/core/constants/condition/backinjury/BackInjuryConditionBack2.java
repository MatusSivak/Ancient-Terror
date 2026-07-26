package sk.sivak.eldritchhorror.core.constants.condition.backinjury;

import sk.sivak.eldritchhorror.core.constants.condition.AbstractConditionBack;

public class BackInjuryConditionBack2 extends AbstractConditionBack {

    public BackInjuryConditionBack2() {
        title = "Muscle Spasms";
        flavorText = "Your hand begins to shake uncontrollably, breaking whatever you are holding. " +
                "Your whole body is overcome by tremors.";
        effectText = "[#BAD]Discard 1 Item possession and become Delayed.[]\n" +
                "[#GOOD]Then discard this card.[]";
    }
}
