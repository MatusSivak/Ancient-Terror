package sk.sivak.eldritchhorror.core.constants.condition.lostintimeandspace;

import sk.sivak.eldritchhorror.core.constants.condition.AbstractConditionBack;

public class LostInTimeAndSpaceConditionBack4 extends AbstractConditionBack {

    public LostInTimeAndSpaceConditionBack4() {
        title = "Guide From The Void";

        flavorText = "The silhouette of a man towers before you, standing behind a veil of shimmering stars. " +
                "You hear the being's voice in your mind, offering an escape from this emptiness.";

        effectText = "You may gain a [#BAD]Dark Pact Condition[] to\n" +
                "[#GOOD]place your Investigator on a space of your choice.[]\n" +
                "If you do not gain the Condition,\n" +
                "[#BAD]spawn 1 Gate, place your Investigator on that space,\n" +
                "and advance the Omen by 1.[]\n" +
                "[#GOOD]Then discard this card.[]";
    }
}
