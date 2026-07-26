package sk.sivak.eldritchhorror.core.constants.condition.lostintimeandspace;

import sk.sivak.eldritchhorror.core.constants.condition.AbstractConditionBack;

public class LostInTimeAndSpaceConditionBack2 extends AbstractConditionBack {

    public LostInTimeAndSpaceConditionBack2() {
        title = "Strange Worlds";

        flavorText = "You wander from one strange world to another, always at the edge of existence. " +
                "You believe it is possible to open a portal that can return you to the real world.";

        effectText = "Test Lore-1. If you pass,\n" +
                "[#GOOD]place your investigator on a space of your choice.[]\n" +
                "If you fail, [#BAD]spawn 1 Gate and place your Investigator on that space.\n" +
                "Then encounter that Gate.[]\n" +
                "[#GOOD]Then discard this card.[]";
    }
}
