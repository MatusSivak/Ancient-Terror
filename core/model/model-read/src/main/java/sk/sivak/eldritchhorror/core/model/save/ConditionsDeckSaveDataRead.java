package sk.sivak.eldritchhorror.core.model.save;

import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;

import java.util.List;
import java.util.Map;

public interface ConditionsDeckSaveDataRead {
    Map<String, List<? extends ConditionInfoSaveDataRead>> getInvestigatorConditionInfoMap();

    interface ConditionInfoSaveDataRead {
        ConditionId getConditionId();

        int getConditionBackId();

    }
}
