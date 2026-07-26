package sk.sivak.eldritchhorror.core.action.impl.saveload;

import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.model.save.SaveDataRead;

public class LoadAncientOneHelper {

    public static void loadAncientOne(SaveDataRead saveData) {
        ServicePlatform.get().getModel().getAncientOne().load(saveData.getAncientOne());
        ServicePlatform.get().getEventListenerProvider().loadAncientOneListeners(ServicePlatform.get().getModel().getAncientOne().getAncientOneInfo());
        ServicePlatform.get().getResearchEncounterDeck().init(saveData.getAncientOne().getAncientOneId());
    }
}
