package sk.sivak.eldritchhorror.core.constants.condition.righteous;

import sk.sivak.eldritchhorror.core.constants.condition.AbstractConditionBack;

public class RighteousConditionBack4 extends AbstractConditionBack {

    public RighteousConditionBack4() {
        title = "Daring Rescue";
        flavorText = "The sound of the shot rings through the marble halls. " +
                "The beast writhes and lurches forward again. " +
                "Another well placed shot puts the foul creature down for good. " +
                "These beasts will claim no more lives on your watch.";
        effectText = "[#GOOD]One Monster of your choice loses five Health.[]\n" +
                "[#BAD]Then discard this card.[]";
    }
}
