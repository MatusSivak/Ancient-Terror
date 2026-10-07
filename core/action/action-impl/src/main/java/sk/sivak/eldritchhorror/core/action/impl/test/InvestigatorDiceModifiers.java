package sk.sivak.eldritchhorror.core.action.impl.test;

import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionInfo;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionTrait;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.test.TestFlavorType;
import sk.sivak.eldritchhorror.core.model.InvestigatorRead;

/** Applies bonuses to each newly rolled die, including rerolls, before it is displayed. */
public final class InvestigatorDiceModifiers {
    private InvestigatorDiceModifiers() {}

    public static int roll(ConditionInfo condition) {
        if (condition == null && ServicePlatform.get().getTestFlavor().getFlavorType() == TestFlavorType.CONDITION)
            condition = ServicePlatform.get().getTestFlavor().getFlavorData();
        InvestigatorRead investigator = ServicePlatform.get().getInvestigators().getActiveInvestigator();
        return adjust(ServicePlatform.get().getModel().rollDie(), investigator.getInfo().getInvestigatorId(),
                investigator.isLostInTimeAndSpace(), condition);
    }

    public static int adjust(int value, InvestigatorId investigator, boolean offBoard, ConditionInfo condition) {
        if (investigator == InvestigatorId.THE_NUN && !offBoard && condition != null
                && (condition.getTraits().contains(ConditionTrait.BOON) || condition.getTraits().contains(ConditionTrait.BANE)))
            return Math.min(6, value + 1);
        return value;
    }
}

