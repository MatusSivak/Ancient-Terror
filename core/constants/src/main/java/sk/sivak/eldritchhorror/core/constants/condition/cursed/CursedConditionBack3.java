package sk.sivak.eldritchhorror.core.constants.condition.cursed;

import sk.sivak.eldritchhorror.core.constants.condition.AbstractConditionBack;

public class CursedConditionBack3 extends AbstractConditionBack {

    public CursedConditionBack3() {
        title = "Tainted";
        flavorText = "As soon as people see you, " +
                "they avert their eyes and pull their loved ones away from you. " +
                "They all fear being stained by the foulness that has contaminated your soul.";
        effectText = "[#BAD]Another investigator of your choice\n" +
                "that does not have a Cursed Condition\n" +
                "gains a Cursed Condition.\n" +
                "Then flip this card.[]";
    }
}
