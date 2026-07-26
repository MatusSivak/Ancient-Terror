package sk.sivak.eldritchhorror.core.constants.condition.backinjury;

import sk.sivak.eldritchhorror.core.constants.condition.AbstractConditionBack;

public class BackInjuryConditionBack3 extends AbstractConditionBack {

    public BackInjuryConditionBack3() {
        title = "Fractured Spine";
        flavorText = "Searing pain suddenly runs up and down your spine. " +
                "You can barely lift your arms, let alone carry a lot of equipment. " +
                "It is just too painful to carry them all.";
        effectText = "[#BAD]Discard all but 1 of your Item possessions.[]\n" +
                "[#GOOD]Then discard this card.[]";
    }
}
