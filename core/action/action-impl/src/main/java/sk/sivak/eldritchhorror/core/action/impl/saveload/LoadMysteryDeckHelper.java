package sk.sivak.eldritchhorror.core.action.impl.saveload;

import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.constants.ancientone.AncientOneId;
import sk.sivak.eldritchhorror.core.constants.ancientone.AncientOneInfo;
import sk.sivak.eldritchhorror.core.model.save.MysteryDeckSaveDataRead;
import sk.sivak.eldritchhorror.core.model.save.SaveDataRead;

public class LoadMysteryDeckHelper {

    public static void loadMysteryDeck(SaveDataRead saveData) {
        AncientOneInfo ancientOneInfo = ServicePlatform.get().getModel().getAncientOne().getAncientOneInfo();
        AncientOneId ancientOneId = ancientOneInfo.getAncientOneId();
        int players = ServicePlatform.get().getModel().getReferenceCard().getPlayers();
        ServicePlatform.get().getMysteryDeck().initMysteryDeck(ancientOneId, players);
        MysteryDeckSaveDataRead mysteryDeckSaveData = saveData.getMysteryDeck();
        ServicePlatform.get().getMysteryDeck().load(mysteryDeckSaveData);
        ServicePlatform.get().getMysteryDeck().drawNewMystery();
        ServicePlatform.get().getEventListenerProvider().justRegisterMysteryListeners(
                ServicePlatform.get().getMysteryDeck().getCurrentMysteryCard(), mysteryDeckSaveData.getProgress());
    }
}
