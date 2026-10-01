package sk.sivak.eldritchhorror.core.constants.condition.internalinjury;

import sk.sivak.eldritchhorror.core.constants.condition.AbstractConditionInfo;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionTrait;

public class InternalInjuryCondition extends AbstractConditionInfo {

    public InternalInjuryCondition() {
        traits.add(ConditionTrait.INJURY);
    }

    @Override
    public String getName() {
        return "Internal Injury";
    }

    @Override
    public String getDescription() {
        return "Rest: you may roll 1 die.\n" +
                "5 or 6 → discard this card.\n" +
                "\n" +
                "RECKONING: Test Strength.\n" +
                "Fail → flip this card.";
    }

    @Override
    public ConditionId getId() {
        return ConditionId.INTERNAL_INJURY;
    }

    @Override
    public boolean hasReckoning() {
        return true;
    }
}
