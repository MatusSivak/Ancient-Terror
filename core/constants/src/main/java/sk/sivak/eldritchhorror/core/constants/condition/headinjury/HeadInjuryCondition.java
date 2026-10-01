package sk.sivak.eldritchhorror.core.constants.condition.headinjury;

import sk.sivak.eldritchhorror.core.constants.condition.AbstractConditionInfo;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionTrait;

public class HeadInjuryCondition extends AbstractConditionInfo {

    public HeadInjuryCondition() {
        traits.add(ConditionTrait.INJURY);
    }

    @Override
    public String getName() {
        return "Head Injury";
    }

    @Override
    public String getDescription() {
        return "Rest: you cannot recover Sanity.\n" +
                "Roll 1 die: 4, 5 or 6 → discard this card.\n" +
                "\n" +
                "RECKONING: Test Strength.\n" +
                "Fail → flip this card.";
    }

    @Override
    public ConditionId getId() {
        return ConditionId.HEAD_INJURY;
    }

    @Override
    public boolean hasReckoning() {
        return true;
    }
}
