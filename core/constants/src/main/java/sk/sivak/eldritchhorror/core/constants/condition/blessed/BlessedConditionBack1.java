package sk.sivak.eldritchhorror.core.constants.condition.blessed;

import sk.sivak.eldritchhorror.core.constants.condition.AbstractConditionBack;

public class BlessedConditionBack1 extends AbstractConditionBack {

    public BlessedConditionBack1() {
        title = "Anointed";
        flavorText = "You feel that you truly have an important role in the events of the world. " +
                "It is your calling to share the hope and courage you feel " +
                "with all the others who stand against the darkness.";
        effectText = "[#GOOD]Another investigator of your choice\n" +
                "that does not have a Blessed Condition\n" +
                "gains a Blessed Condition.\n" +
                "Then flip this card.[]";
    }
}
