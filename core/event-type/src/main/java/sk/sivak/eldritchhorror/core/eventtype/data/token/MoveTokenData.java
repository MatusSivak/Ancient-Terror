package sk.sivak.eldritchhorror.core.eventtype.data.token;

import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.constants.monster.MonsterInfo;

public class MoveTokenData {
    private LocationId spawnLocationId;
    private LocationId targetLocationId;

    public MoveTokenData(LocationId spawnLocationId, LocationId targetLocationId) {
        this.spawnLocationId = spawnLocationId;
        this.targetLocationId = targetLocationId;
    }

    public LocationId getSpawnLocationId() {
        return spawnLocationId;
    }

    public LocationId getTargetLocationId() {
        return targetLocationId;
    }
}
