package sk.sivak.eldritchhorror.core.constants.condition.darkpact;

import sk.sivak.eldritchhorror.core.constants.condition.AbstractConditionBack;

public class DarkPactConditionBack2 extends AbstractConditionBack {

    public DarkPactConditionBack2() {
        title = "Nightmare Meeting";
        flavorText = "The hooded figure approaches you, gliding as if floating just above the ground. " +
                "You hear the figure's rattling voice in your mind, probing for the names of your companions. " +
                "Suddenly, you awake in a cold sweat.";
        effectText = "[#BAD]Each other investigator\n" +
                "gains a Cursed Condition\n" +
                "unless he gains a Dark Pact Condition.\n" +
                "[#GOOD]Then discard this card.[]";
    }
}
