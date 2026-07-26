package sk.sivak.eldritchhorror.core.constants.condition.backinjury;

import sk.sivak.eldritchhorror.core.constants.condition.AbstractConditionInfo;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionTrait;

public class BackInjuryCondition extends AbstractConditionInfo {

    public BackInjuryCondition() {
        traits.add(ConditionTrait.INJURY);
    }

    @Override
    public String getName() {
        return "Back Injury";
    }

    @Override
    public String getDescription() {
        return "When you perform a Rest action,\n" +
                "you may roll 1 die.\n" +
                "On a 5 or 6, discard this card.\n\n" +
                "RECKONING: Test Strength.\n" +
                "If you fail, flip this card.";
    }

    @Override
    public ConditionId getId() {
        return ConditionId.BACK_INJURY;
    }

    @Override
    public boolean hasReckoning() {
        return true;
    }
}
