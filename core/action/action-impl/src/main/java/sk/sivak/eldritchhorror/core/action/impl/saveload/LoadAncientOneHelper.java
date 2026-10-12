package sk.sivak.eldritchhorror.core.action.impl.saveload;

import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.model.save.SaveDataRead;
import sk.sivak.eldritchhorror.core.constants.ancientone.AncientOneId;
import sk.sivak.eldritchhorror.core.constants.ancientone.AncientOneInfo;

public class LoadAncientOneHelper {

    public static void loadAncientOne(SaveDataRead saveData) {
        ServicePlatform.get().getModel().getAncientOne().load(saveData.getAncientOne());
        AncientOneInfo info = ServicePlatform.get().getModel().getAncientOne().getAncientOneInfo();
        // Older awakened saves stored their remaining time as Ancient One power.
        if (info.getAncientOneId() == AncientOneId.YIG && info.isAwaken() && info.getPower() > 0) {
            ServicePlatform.get().getDoomTrack().initDoomTrack(-info.getPower());
            info.setPower(0);
        }
        ServicePlatform.get().getEventListenerProvider().loadAncientOneListeners(ServicePlatform.get().getModel().getAncientOne().getAncientOneInfo());
        ServicePlatform.get().getResearchEncounterDeck().init(saveData.getAncientOne().getAncientOneId());
    }
}
