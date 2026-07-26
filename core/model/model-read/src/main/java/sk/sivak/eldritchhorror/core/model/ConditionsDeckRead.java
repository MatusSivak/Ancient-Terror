package sk.sivak.eldritchhorror.core.model;

import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionInfo;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionTrait;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;

import java.util.List;

public interface ConditionsDeckRead {
    boolean hasCondition(InvestigatorId investigatorId, ConditionId conditionId);

    ConditionInfo getCondition(InvestigatorId investigatorId, ConditionId conditionId);

    List<ConditionInfo> getCondition(InvestigatorId investigatorId, ConditionTrait conditionTrait);

    boolean hasAnyCondition(InvestigatorId investigatorId);

    boolean hasTrait(InvestigatorId investigatorId, ConditionTrait trait);

    List<ConditionInfo> getConditions(InvestigatorId investigatorId);

    boolean canGetTrait(InvestigatorId investigatorId, ConditionTrait trait);

    boolean canGetConditionId(InvestigatorId investigatorId, ConditionId conditionId);
}
