package sk.sivak.eldritchhorror.core.eventtype.data.monster;

import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.constants.monster.MonsterInfo;

public class MoveMonsterData {
    private MonsterInfo monsterInfo;
    private LocationId targetLocationId;

    public MoveMonsterData(MonsterInfo monsterInfo, LocationId targetLocationId) {
        this.monsterInfo = monsterInfo;
        this.targetLocationId = targetLocationId;
    }

    public MonsterInfo getMonsterInfo() {
        return monsterInfo;
    }

    public LocationId getTargetLocationId() {
        return targetLocationId;
    }
}
