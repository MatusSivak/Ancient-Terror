package sk.sivak.eldritchhorror.core.constants.condition.blessed;

import sk.sivak.eldritchhorror.core.constants.condition.AbstractConditionInfo;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionTrait;

public class BlessedCondition extends AbstractConditionInfo {
    public BlessedCondition() {
        traits.add(ConditionTrait.BOON);
    }

    @Override
    public String getName() {
        return "Blessed";
    }

    @Override
    public String getDescription() {
        return "4, 5, and 6's count as successes on your tests.\n" +
                "If you would gain another Blessed Condition, flip this card instead.\n" +
                "If you would gain a Cursed\nCondition, discard this card instead.\n" +
                "RECKONING: Roll 1 die.\nOn a 1 or 2, discard this card.";
    }

    @Override
    public ConditionId getId() {
        return ConditionId.BLESSED;
    }

    @Override
    public boolean hasReckoning() {
        return true;
    }
}
