package sk.sivak.eldritchhorror.core.constants.condition.amnesia;

import sk.sivak.eldritchhorror.core.constants.condition.AbstractConditionInfo;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionTrait;

public class AmnesiaCondition extends AbstractConditionInfo {

    public AmnesiaCondition() {
        traits.add(ConditionTrait.MADNESS);
    }

    @Override
    public String getName() {
        return "Amnesia";
    }

    @Override
    public String getDescription() {
        return "Rest: you may roll 1 die.\n" +
                "5 or 6 → discard this card.\n" +
                "\n" +
                "RECKONING: Test Will.\n" +
                "Fail → flip this card.";
    }

    @Override
    public ConditionId getId() {
        return ConditionId.AMNESIA;
    }

    @Override
    public boolean hasReckoning() {
        return true;
    }
}
