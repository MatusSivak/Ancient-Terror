package sk.sivak.eldritchhorror.core.constants.condition.amnesia;

import sk.sivak.eldritchhorror.core.constants.condition.AbstractConditionBack;

public class AmnesiaConditionBack3 extends AbstractConditionBack {

    public AmnesiaConditionBack3() {
        title = "Forgotten Crimes";
        flavorText = "You don't want to believe it's true, but the evidence is irrefutable. " +
                "At some point that you no longer recall, you went on a crime spree. " +
                "Now you must accompany the police.";
        effectText = "[#BAD]Gain a Detained Condition.[]\n" +
                "[#GOOD]Then discard this card.[]";
    }
}
