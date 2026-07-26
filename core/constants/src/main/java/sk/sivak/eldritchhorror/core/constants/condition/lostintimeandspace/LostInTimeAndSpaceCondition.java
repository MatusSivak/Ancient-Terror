package sk.sivak.eldritchhorror.core.constants.condition.lostintimeandspace;

import sk.sivak.eldritchhorror.core.constants.condition.AbstractConditionInfo;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;

public class LostInTimeAndSpaceCondition extends AbstractConditionInfo {

    @Override
    public ConditionId getId() {
        return ConditionId.LOST_IN_TIME_AND_SPACE;
    }

    @Override
    public String getName() {
        return "Lost in Time and Space";
    }

    @Override
    public String getDescription() {
        return "Remove your Investigator token\n" +
                "from the game board.\n\n" +
                "You are unaffected by other game effects and\n" +
                "cannot move or perform actions.\n\n" +
                "Instead of resolving an encounter,\n" +
                "flip this card.";
    }
}
