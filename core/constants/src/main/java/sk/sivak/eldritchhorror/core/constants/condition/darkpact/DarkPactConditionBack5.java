package sk.sivak.eldritchhorror.core.constants.condition.darkpact;

import sk.sivak.eldritchhorror.core.constants.condition.AbstractConditionBack;

public class DarkPactConditionBack5 extends AbstractConditionBack {

    public DarkPactConditionBack5() {
        title = "One of the Thousand";
        flavorText = "The chanting reaches a fever pitch. " +
                "The cult leader places a ritual dagger in your hand and tells you, " +
                "\"The time has come. You must pay the blood you owe to the Children of the Black Goat.\"";
        effectText = "[#BAD]Another investigator is devoured.[]\n" +
                "[#GOOD]Then discard this card.[]";
    }
}
