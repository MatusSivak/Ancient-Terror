package sk.sivak.eldritchhorror.core.constants.condition.internalinjury;

import sk.sivak.eldritchhorror.core.constants.condition.AbstractConditionBack;

public class InternalInjuryConditionBack3 extends AbstractConditionBack {

    public InternalInjuryConditionBack3() {
        title = "Excruciating Pain";
        flavorText = "The pain rises to a crescendo, knocking you off your feet. " +
                "When you finally regain consciousness, the floor is covered in blood.";
        effectText = "[#BAD]Lose 3 Health.[]\n" +
                "[#GOOD]Then discard this card.[]";
    }
}
