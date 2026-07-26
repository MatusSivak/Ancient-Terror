package sk.sivak.eldritchhorror.core.eventlistener.monster;

import sk.sivak.eldritchhorror.core.constants.monster.MonsterInfo;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;

import static sk.sivak.eldritchhorror.core.eventtype.DirectEvent.RECKONING_MONSTER;

public class ShoggothMonsterListener extends AbstractMonsterListener {

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
            if (monsterInfo.getCurrentHealth().equals(monsterInfo.getToughness())) {
                return;
            }
            int amount = monsterInfo.getToughness() - monsterInfo.getCurrentHealth();
            ServicePlatform.get().getDoomOmenService().onTriggeredReckoning();
            ServicePlatform.get().getService().moveCameraToLocation(monsterInfo.getCurrentLocation());
            ServicePlatform.get().getMonsterService().restoreHealth(monsterInfo, amount, false, true);
        }
    }

    @Override
    public void unregister() {
        ServicePlatform.get().getEventQueue().unregisterListener(reckoningListener);
    }
}
