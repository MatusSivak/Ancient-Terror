package sk.sivak.eldritchhorror.core.constants.condition.debt;

import sk.sivak.eldritchhorror.core.constants.condition.AbstractConditionInfo;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionTrait;

public class DebtCondition extends AbstractConditionInfo {

    // TODO LATER add AGREEMENT card back
    public DebtCondition() {
        traits.add(ConditionTrait.DEAL);
        traits.add(ConditionTrait.COMMON);
    }

    @Override
    public String getName() {
        return "Debt";
    }

    @Override
    public String getDescription() {
        return "LOCAL ACTION: Test Influence.\n" +
                "Pass → discard this card.\n" +
                "\n" +
                "RECKONING: Collectors come\n" +
                "for your debt → flip this card.";
    }

    @Override
    public ConditionId getId() {
        return ConditionId.DEBT;
    }

    @Override
    public boolean hasReckoning() {
        return true;
    }
}
