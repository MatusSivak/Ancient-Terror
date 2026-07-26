package sk.sivak.eldritchhorror.core.constants.condition.righteous;

import sk.sivak.eldritchhorror.core.constants.condition.AbstractConditionInfo;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionTrait;

public class RighteousCondition extends AbstractConditionInfo {

    public RighteousCondition() {
        traits.add(ConditionTrait.BOON);
    }

    @Override
    public String getName() {
        return "Righteous";
    }

    @Override
    public String getDescription() {
        return "When you perform\n" +
                "a Focus action or a Rest action,\n" +
                "gain 1 Focus or recover 1 Sanity.\n\n" +
                "RECKONING:\n" +
                "You may flip this card.";
    }

    @Override
    public ConditionId getId() {
        return ConditionId.RIGHTEOUS;
    }

    @Override
    public boolean hasReckoning() {
        return true;
    }
}
