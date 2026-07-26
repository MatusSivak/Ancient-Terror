package sk.sivak.eldritchhorror.core.model;

import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionInfo;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionTrait;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.model.save.ConditionsDeckSaveDataRead;
import sk.sivak.eldritchhorror.core.model.save.SaveDataWrite;

public interface ConditionsDeckWrite extends ConditionsDeckRead {

    void createDeck();

    ConditionInfo gainCondition(ConditionId conditionId, InvestigatorId investigatorId);

    ConditionInfo gainCondition(ConditionTrait conditionTrait, InvestigatorId investigatorId);

    void discard(InvestigatorId investigatorId, ConditionInfo conditionInfo);

    SaveDataWrite.ConditionsDeckSaveDataWrite save();

    void load(ConditionsDeckSaveDataRead conditionsDeckSaveData);
}
