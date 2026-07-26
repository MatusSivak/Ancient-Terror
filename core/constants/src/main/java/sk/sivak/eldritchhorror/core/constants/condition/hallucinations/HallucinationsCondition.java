package sk.sivak.eldritchhorror.core.constants.condition.hallucinations;

import sk.sivak.eldritchhorror.core.constants.condition.AbstractConditionInfo;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionTrait;

public class HallucinationsCondition extends AbstractConditionInfo {

    public HallucinationsCondition() {
        traits.add(ConditionTrait.MADNESS);
    }

    @Override
    public String getName() {
        return "Hallucinations";
    }

    @Override
    public String getDescription() {
        return "When you perform a Rest action,\n" +
                "you may roll 1 die.\n" +
                "On a 5 or 6, discard this card.\n\n" +
                "RECKONING: Test Will.\n" +
                "If you fail, flip this card.";
    }

    @Override
    public ConditionId getId() {
        return ConditionId.HALLUCINATIONS;
    }

    @Override
    public boolean hasReckoning() {
        return true;
    }
}
