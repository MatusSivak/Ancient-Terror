package sk.sivak.eldritchhorror.core.action.impl.saveload;

import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.constants.monster.MonsterInfo;
import sk.sivak.eldritchhorror.core.model.save.SaveDataRead;

public class LoadMonsteCupHelper {

    public static void loadMonsterCup(SaveDataRead saveData) {
        ServicePlatform.get().getMonsterCup().createMonsterCup();
        ServicePlatform.get().getMonsterCup().removeFromCup(
                ServicePlatform.get().getModel().getAncientOne().getAncientOneInfo().getRemovedMonsters());

        ServicePlatform.get().getMonsterCup().load(saveData.getMonsterCup());
        for (MonsterInfo monster : ServicePlatform.get().getMonsterCup().getMonsters()) {
            ServicePlatform.get().getEventListenerProvider().justRegisterMonsterListeners(monster);
        }
    }
}
