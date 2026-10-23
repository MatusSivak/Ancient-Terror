package sk.sivak.eldritchhorror.core.eventtype.data.monster;

import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.constants.monster.MonsterInfo;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MoveMonsterData {
    private final List<MonsterInfo> monsters;
    private LocationId targetLocationId;

    public MoveMonsterData(MonsterInfo monsterInfo, LocationId targetLocationId) {
        this(Collections.singletonList(monsterInfo), targetLocationId);
    }

    /** A group shares one origin and one animation/camera journey. */
    public MoveMonsterData(List<? extends MonsterInfo> monsters, LocationId targetLocationId) {
        if(monsters.isEmpty())throw new IllegalArgumentException("Empty monster group");
        LocationId origin=monsters.get(0).getCurrentLocation();
        for(MonsterInfo monster:monsters) {
            if(monster.getCurrentLocation()!=origin)throw new IllegalArgumentException("Monster group must share an origin");
        }
        this.monsters = Collections.unmodifiableList(new ArrayList<>(monsters));
        this.targetLocationId = targetLocationId;
    }

    public MonsterInfo getMonsterInfo() {
        return monsters.get(0);
    }

    public List<MonsterInfo> getMonsters() { return monsters; }

    public LocationId getTargetLocationId() {
        return targetLocationId;
    }
}
