package sk.sivak.eldritchhorror.core.constants.condition.lostintimeandspace;

import sk.sivak.eldritchhorror.core.constants.condition.AbstractConditionInfo;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionTrait;

public class LostInTimeAndSpaceCondition extends AbstractConditionInfo {

    public LostInTimeAndSpaceCondition() {
        traits.add(ConditionTrait.RESTRICTION);
    }

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
        return "Remove your token from the board.\n" +
                "You are unaffected by other effects\n" +
                "and cannot move or perform actions.\n" +
                "\n" +
                "Instead of an encounter → flip this card.";
    }
}
