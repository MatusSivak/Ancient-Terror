package sk.sivak.eldritchhorror.core.constants.condition.darkpact;

import sk.sivak.eldritchhorror.core.constants.condition.AbstractConditionInfo;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionTrait;

public class DarkPactCondition extends AbstractConditionInfo{

    public DarkPactCondition() {
        traits.add(ConditionTrait.DEAL);
    }

    @Override
    public ConditionId getId() {
        return ConditionId.DARK_PACT;
    }

    @Override
    public String getName() {
        return "Dark Pact";
    }

    @Override
    public boolean hasReckoning() {
        return true;
    }

    @Override
    public String getDescription() {
        return "RECKONING: Roll 1 die.\n" +
                "1 → your bargain comes due:\n" +
                "flip this card.";
    }
}
