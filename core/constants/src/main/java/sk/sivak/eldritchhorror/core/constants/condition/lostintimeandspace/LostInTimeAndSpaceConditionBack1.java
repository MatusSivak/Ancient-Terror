package sk.sivak.eldritchhorror.core.constants.condition.lostintimeandspace;

import sk.sivak.eldritchhorror.core.constants.condition.AbstractConditionBack;

public class LostInTimeAndSpaceConditionBack1 extends AbstractConditionBack {

    public LostInTimeAndSpaceConditionBack1() {
        title = "Swarming Masses";
        flavorText = "Among a swirling mass of stars, you discover swarms of alien beings that feast upon one another without hesitation. " +
                "You try to find your way home without being noticed.";
        effectText = "Test Observation-1. If you pass,\n" +
                "[#GOOD]place your Investigator on a space of your choice.[]\n" +
                "If you fail, [#BAD]spawn 1 Gate and resolve a Monster Surge on that space.[]\n" +
                "Then place your Investigator on that space and [#BAD]become Delayed.[]\n" +
                "[#GOOD]Then discard this card.[]";
    }
}
