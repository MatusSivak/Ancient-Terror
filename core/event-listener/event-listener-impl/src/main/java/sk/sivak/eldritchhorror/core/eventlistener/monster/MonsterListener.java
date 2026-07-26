package sk.sivak.eldritchhorror.core.eventlistener.monster;

import sk.sivak.eldritchhorror.core.constants.monster.MonsterInfo;

public interface MonsterListener {

    void register(MonsterInfo monsterInfo);

    void justRegisterListeners(MonsterInfo monsterInfo);

    void unregister();
}
