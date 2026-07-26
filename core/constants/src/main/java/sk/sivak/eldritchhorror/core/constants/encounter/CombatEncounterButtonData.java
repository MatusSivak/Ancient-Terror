package sk.sivak.eldritchhorror.core.constants.encounter;

import sk.sivak.eldritchhorror.core.constants.monster.MonsterInfo;

public class CombatEncounterButtonData extends EncounterButtonData {
    private MonsterInfo monsterInfo;

    public CombatEncounterButtonData(String uuid, MonsterInfo monsterInfo) {
        super(uuid);
        this.monsterInfo = monsterInfo;
    }

    public MonsterInfo getMonsterInfo() {
        return monsterInfo;
    }
}
