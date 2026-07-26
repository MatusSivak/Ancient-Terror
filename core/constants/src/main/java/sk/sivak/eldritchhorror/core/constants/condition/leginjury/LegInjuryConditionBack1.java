package sk.sivak.eldritchhorror.core.constants.condition.leginjury;

import sk.sivak.eldritchhorror.core.constants.condition.AbstractConditionBack;

public class LegInjuryConditionBack1 extends AbstractConditionBack {

    public LegInjuryConditionBack1() {
        title = "Infected Wound";
        flavorText = "As you remove the blood-soaked bandages, the wound oozes a foul yellow puss. " +
                "The pungent scent of the infection causes your head to swim, and you nearly vomit.";
        effectText = "[#BAD]Gain an Illness Condition.[]\n" +
                "[#GOOD]Then discard this card.[]";
    }
}
