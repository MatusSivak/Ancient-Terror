package sk.sivak.eldritchhorror.core.eventlistener.mythos;

import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventtype.data.SpawnMonsterData;

public class YellowMythosCard1 implements MythosCardEventListener{

    @Override
    public void execute() {
        for (int i = 0; i < 2; i++) {
            SpawnMonsterData spawnMonsterData = new SpawnMonsterData();
            spawnMonsterData.setOverrideSpawnLocation(false);
            spawnMonsterData.setLocationId(ServicePlatform.get().getExpeditionDeck().getExpeditionTokenLocation());
            ServicePlatform.get().getGameService().spawnMonster(spawnMonsterData);
        }
    }
}
