package sk.sivak.eldritchhorror.core.constants.condition.cursed;

import sk.sivak.eldritchhorror.core.constants.condition.AbstractConditionInfo;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionTrait;

public class CursedCondition extends AbstractConditionInfo {
    public CursedCondition() {
        traits.add(ConditionTrait.BANE);
    }

    @Override
    public String getName() {
        return "Cursed";
    }

    @Override
    public String getDescription() {
        return "Only 6's count as successes on your tests.\n" +
                "If you would gain another Cursed Condition, flip this card instead.\n" +
                "If you would gain a Blessed Condition, discard this card instead.\n" +
                "RECKONING: Roll 1 die.\nOn a 4, 5 or 6, discard this card.";
    }

    @Override
    public ConditionId getId() {
        return ConditionId.CURSED;
    }

    @Override
    public boolean hasReckoning() {
        return true;
    }
}
