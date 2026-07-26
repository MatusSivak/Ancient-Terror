package sk.sivak.eldritchhorror.core.action.impl.saveload;

import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionInfo;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.model.save.SaveDataRead;

public class LoadConditionsDeckHelper {
    public static void loadConditionsDeck(SaveDataRead saveData) {
        ServicePlatform.get().getConditionsDeck().load(saveData.getConditionsDeck());
        for (String investigatorIdAsString : saveData.getConditionsDeck().getInvestigatorConditionInfoMap().keySet()) {
            InvestigatorId investigatorId = InvestigatorId.valueOf(investigatorIdAsString);
            for (ConditionInfo conditionInfo : ServicePlatform.get().getConditionsDeck().getConditions(investigatorId)) {
                ServicePlatform.get().getConditionListenerProvider().getConditionListener(conditionInfo).register(conditionInfo, investigatorId);
            }
        }
    }
}
