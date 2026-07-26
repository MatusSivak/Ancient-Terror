package sk.sivak.eldritchhorror.core.eventtype.data;

import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.constants.monster.MonsterId;
import sk.sivak.eldritchhorror.core.constants.monster.MonsterInfo;

public class SpawnMonsterData {
    private LocationId locationId;
    private MonsterId monsterId;
    private MonsterInfo monsterInfo;
    private boolean overrideSpawnLocation = false;

    public SpawnMonsterData() {
    }

    public LocationId getLocationId() {
        return locationId;
    }

    public void setLocationId(LocationId locationId) {
        this.locationId = locationId;
    }

    public MonsterInfo getMonsterInfo() {
        return monsterInfo;
    }

    public void setMonsterInfo(MonsterInfo monsterInfo) {
        this.monsterInfo = monsterInfo;
    }

    public MonsterId getMonsterId() {
        return monsterId;
    }

    public void setMonsterId(MonsterId monsterId) {
        this.monsterId = monsterId;
    }

    public boolean isOverrideSpawnLocation() {
        return overrideSpawnLocation;
    }

    public void setOverrideSpawnLocation(boolean overrideSpawnLocation) {
        this.overrideSpawnLocation = overrideSpawnLocation;
    }
}
