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
        return "Your tests succeed on 4, 5 or 6.\n" +
                "Gain another Blessed → flip this card.\n" +
                "Gain a Cursed → discard this card.\n" +
                "RECKONING: Roll 1 die.\n" +
                "1 or 2 → discard this card.";
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
