package sk.sivak.eldritchhorror.core.action.impl.saveload;

import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.constants.RumorCardInfo;
import sk.sivak.eldritchhorror.core.model.save.RumorsSaveDataRead;
import sk.sivak.eldritchhorror.core.model.save.SaveDataRead;

public class LoadRumorsHelper {

    public static void loadRumors(SaveDataRead saveData) {
        if (saveData.getRumors() == null) {
            return;
        }
        ServicePlatform.get().getRumors().load(saveData.getRumors());
        for (RumorsSaveDataRead.ActiveRumorSaveDataRead savedActiveRumor : saveData.getRumors().getActiveRumors()) {
            String rumorId = savedActiveRumor.getRumorId();
            ServicePlatform.get().getEventListenerProvider().justLoadRumor(rumorId);
        }

    }
}
