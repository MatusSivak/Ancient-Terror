package sk.sivak.eldritchhorror.core.constants.condition.lostintimeandspace;

import sk.sivak.eldritchhorror.core.constants.condition.AbstractConditionBack;

public class LostInTimeAndSpaceConditionBack3 extends AbstractConditionBack {

    public LostInTimeAndSpaceConditionBack3() {
        title = "Replaced by Another";

        flavorText = "In the space between worlds, you encounter a being that can change its shape. " +
                "The creature seems fascinated with you and asks you countless questions.";

        effectText = "Test Influence-1. If you pass,\n" +
                "[#GOOD]place your Investigator on a space of your choice.[]\n" +
                "If you fail, [#BAD]spawn 1 Gate and spawn the Doppelganger Epic Monster on that space.\n" +
                "Then place your Investigator on a random space.[]\n" +
                "[#GOOD]Then discard this card.[]";
    }
}
