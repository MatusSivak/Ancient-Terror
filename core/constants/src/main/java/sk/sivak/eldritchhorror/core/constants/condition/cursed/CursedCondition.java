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
        return "Your tests succeed only on 6.\n" +
                "Gain another Cursed → flip this card.\n" +
                "Gain a Blessed → discard this card.\n" +
                "RECKONING: Roll 1 die.\n" +
                "4, 5 or 6 → discard this card.";
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
