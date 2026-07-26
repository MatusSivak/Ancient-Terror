package sk.sivak.eldritchhorror.core.constants.condition.poisoned;

import sk.sivak.eldritchhorror.core.constants.condition.AbstractConditionInfo;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionTrait;

public class PoisonedCondition extends AbstractConditionInfo{

    public PoisonedCondition() {
        traits.add(ConditionTrait.ILLNESS);
    }

    @Override
    public ConditionId getId() {
        return ConditionId.POISONED;
    }

    @Override
    public String getName() {
        return "Poisoned";
    }

    @Override
    public String getDescription() {
        return "When you perform a Rest action,\n" +
                "you cannot recover Health nor Sanity.\n" +
                "Flip this card.\n\n" +
                "RECKONING: Lose 1 Health.";
    }
}
