package sk.sivak.eldritchhorror.core.eventlistener.monster;

import sk.sivak.eldritchhorror.core.constants.monster.MonsterInfo;

public abstract class AbstractMonsterListener implements MonsterListener {
    protected MonsterInfo monsterInfo;

    @Override
    public void register(MonsterInfo monsterInfo) {
        this.monsterInfo = monsterInfo;
    }

    @Override
    public void justRegisterListeners(MonsterInfo monsterInfo) {
        register(monsterInfo);
    }

    public abstract void unregister();

    public void beforeSpawnMonsterInit(MonsterInfo monsterInfo) {}
}
