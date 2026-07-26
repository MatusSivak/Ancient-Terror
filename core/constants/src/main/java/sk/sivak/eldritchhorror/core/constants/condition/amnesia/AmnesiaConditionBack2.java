package sk.sivak.eldritchhorror.core.constants.condition.amnesia;

import sk.sivak.eldritchhorror.core.constants.condition.AbstractConditionBack;

public class AmnesiaConditionBack2 extends AbstractConditionBack {

    public AmnesiaConditionBack2() {
        title = "The Terrible Truth";
        flavorText = "Suddenly, the terrible memories of the profane ritual return to you. " +
                "How merciful your mind had been to forget your horrible deeds. " +
                "Now that you remember the truth, you know you will soon have to hold up your end of the bargain.";
        effectText = "[#BAD]Gain a Dark Pact Condition.[]\n" +
                "[#GOOD]Then discard this card.[]";
    }
}
