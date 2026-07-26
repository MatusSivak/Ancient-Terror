package sk.sivak.eldritchhorror.core.model.save;

import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConditionsDeckSaveData implements SaveDataWrite.ConditionsDeckSaveDataWrite {
    private Map<String, List<? extends ConditionInfoSaveDataRead>> investigatorConditionInfoMap = new HashMap<>();

    @Override
    public Map<String, List<? extends ConditionInfoSaveDataRead>> getInvestigatorConditionInfoMap() {
        return investigatorConditionInfoMap;
    }

    public void setInvestigatorConditionInfoMap(Map<String, List<? extends ConditionInfoSaveDataRead>> investigatorConditionInfoMap) {
        this.investigatorConditionInfoMap = investigatorConditionInfoMap;
    }

    public static class ConditionInfoSaveData implements ConditionInfoSaveDataRead{
        private ConditionId conditionId;
        private int conditionBackId;

        // FOR json
        public ConditionInfoSaveData() {
        }

        public ConditionInfoSaveData(ConditionId conditionId, int conditionBackId) {
            this.conditionId = conditionId;
            this.conditionBackId = conditionBackId;
        }

        public void setConditionId(ConditionId conditionId) {
            this.conditionId = conditionId;
        }

        public void setConditionBackId(int conditionBackId) {
            this.conditionBackId = conditionBackId;
        }

        @Override
        public ConditionId getConditionId() {
            return conditionId;
        }

        @Override
        public int getConditionBackId() {
            return conditionBackId;
        }
    }
}