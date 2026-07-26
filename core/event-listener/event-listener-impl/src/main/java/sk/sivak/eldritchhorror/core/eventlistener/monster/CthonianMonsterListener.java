package sk.sivak.eldritchhorror.core.eventlistener.monster;

import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.constants.monster.MonsterInfo;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;

public class CthonianMonsterListener extends AbstractMonsterListener {

    @Override
    public void register(MonsterInfo monsterInfo) {
        super.register(monsterInfo);
        ServicePlatform.get().getService().hold();
        ServicePlatform.get().getMonsterService().highlightSpawnText(monsterInfo);
        ServicePlatform.get().getMonsterService().moveMonster(monsterInfo, LocationId.THE_HEART_OF_AFRICA);
        ServicePlatform.get().getService().release();
    }

    @Override
    public void justRegisterListeners(MonsterInfo monsterInfo) {
        this.monsterInfo = monsterInfo;
    }

    @Override
    public void unregister() {

    }
}
