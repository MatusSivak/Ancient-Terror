package sk.sivak.eldritchhorror.core.constants.condition.headinjury;

import sk.sivak.eldritchhorror.core.constants.condition.AbstractConditionBack;

public class HeadInjuryConditionBack4 extends AbstractConditionBack {

    public HeadInjuryConditionBack4() {
        title = "Concussion";
        flavorText = "You find yourself lying on the ground unable to recall how you got there. " +
                "Trying to remember what happened only causes your head to hurt more.";
        effectText = "[#BAD]Lose 1 Health\n" +
                "and gain an Amnesia Condition.[]\n" +
                "[#GOOD]Then discard this card.[]";
    }
}
