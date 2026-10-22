package sk.sivak.eldritchhorror.core.constants.condition.debt;

import sk.sivak.eldritchhorror.core.constants.condition.AbstractConditionInfo;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionTrait;

public class DebtCondition extends AbstractConditionInfo {

    // Backs 1-8 cover base game + Forsaken Lore. Expansion-only backs (e.g. Agreement) are not part of the game content yet.
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
