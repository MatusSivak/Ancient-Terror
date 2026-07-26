package sk.sivak.eldritchhorror.core.constants.condition.cursed;

import sk.sivak.eldritchhorror.core.constants.condition.AbstractConditionBack;

public class CursedConditionBack6 extends AbstractConditionBack {

    public CursedConditionBack6() {
        title = "Harried";
        flavorText = "The fortune teller explains that you are a beacon, " +
                "visible to all monstrous beings. " +
                "For as long as this curse stands, " +
                "you will be hunted by these creatures. " +
                "You hear a feral growl outside in the dark.";
        effectText = "[#BAD]A Monster ambushes you.\n" +
                "Then, flip this card.[]";
    }
}
