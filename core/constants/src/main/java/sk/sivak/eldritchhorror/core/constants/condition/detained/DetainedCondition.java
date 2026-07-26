package sk.sivak.eldritchhorror.core.constants.condition.detained;

import sk.sivak.eldritchhorror.core.constants.condition.AbstractConditionInfo;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionTrait;

public class DetainedCondition extends AbstractConditionInfo {

    public DetainedCondition() {
        traits.add(ConditionTrait.RESTRICTION);
    }

    @Override
    public String getName() {
        return "Detained";
    }

    @Override
    public String getDescription() {
        return "You cannot move or perform actions\n" +
                "other than the action on this card.\n\n" +
                "Instead of resolving an encounter,\n" +
                "flip this card.\n\n" +
                "LOCAL ACTION: Test Influence.\n" +
                "If you pass, discard this card.";
    }

    @Override
    public ConditionId getId() {
        return ConditionId.DETAINED;
    }

    @Override
    public boolean hasReckoning() {
        return false;
    }
}
