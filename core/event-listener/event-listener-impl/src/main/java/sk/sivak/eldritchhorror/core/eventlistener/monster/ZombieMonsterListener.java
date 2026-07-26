package sk.sivak.eldritchhorror.core.eventlistener.monster;

import sk.sivak.eldritchhorror.core.constants.monster.MonsterInfo;
import sk.sivak.eldritchhorror.core.constants.monster.epic.EpicMonsterId;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventtype.data.SpawnMonsterData;

import static sk.sivak.eldritchhorror.core.eventtype.DirectEvent.RECKONING_MONSTER;

public class ZombieMonsterListener extends AbstractMonsterListener {

    private ReckoningListener reckoningListener;

    @Override
    public void register(MonsterInfo monsterInfo) {
        super.register(monsterInfo);
        reckoningListener = new ReckoningListener();
        ServicePlatform.get().getEventQueue().addDirectEventListener(reckoningListener, RECKONING_MONSTER);
    }

    private class ReckoningListener extends AbstractMonsterReckoningListener {

        @Override
        void onNotify() {
            if (!ServicePlatform.get().getMonsterCup().isEpicMonsterPresent(EpicMonsterId.ZOMBIE_HORDE)) {
                return;
            }
            ServicePlatform.get().getDoomOmenService().onTriggeredReckoning();
            ServicePlatform.get().getService().moveCameraToLocation(monsterInfo.getCurrentLocation());
            ServicePlatform.get().getMonsterService().highlightReckoningText(monsterInfo);
            ServicePlatform.get().getMonsterService().discardMonster(monsterInfo);
            SpawnMonsterData spawnMonsterData = new SpawnMonsterData();
            spawnMonsterData.setLocationId(monsterInfo.getCurrentLocation());
            spawnMonsterData.setMonsterId(EpicMonsterId.ZOMBIE_HORDE);
            ServicePlatform.get().getGameService().spawnMonster(spawnMonsterData);
        }
    }

    @Override
    public void unregister() {
        ServicePlatform.get().getEventQueue().unregisterListener(reckoningListener);
    }
}
