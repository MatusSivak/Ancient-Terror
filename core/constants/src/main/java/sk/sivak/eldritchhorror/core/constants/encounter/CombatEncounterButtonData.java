package sk.sivak.eldritchhorror.core.constants.encounter;

import com.badlogic.gdx.math.Vector2;
import sk.sivak.eldritchhorror.core.constants.monster.MonsterInfo;

public class CombatEncounterButtonData extends EncounterButtonData {
    private MonsterInfo monsterInfo;

    public CombatEncounterButtonData(String uuid, MonsterInfo monsterInfo) {
        super(uuid);
        this.monsterInfo = monsterInfo;
        setOffset(new Vector2(0,-1));
    }

    public MonsterInfo getMonsterInfo() {
        return monsterInfo;
    }
}
