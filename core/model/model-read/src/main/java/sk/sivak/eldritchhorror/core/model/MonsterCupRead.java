package sk.sivak.eldritchhorror.core.model;

import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.constants.monster.AbstractMonsterInfo;
import sk.sivak.eldritchhorror.core.constants.monster.MonsterId;
import sk.sivak.eldritchhorror.core.constants.monster.MonsterInfo;
import sk.sivak.eldritchhorror.core.constants.monster.NonEpicMonsterId;
import sk.sivak.eldritchhorror.core.constants.monster.epic.EpicMonsterId;

import java.util.List;

/**
 * @author msivak
 */
public interface MonsterCupRead {
    boolean containsMonster(LocationId locationId);

    boolean containsMonster(LocationId locationId, MonsterId monsterId);

    List<MonsterInfo> getMonstersAtLocation(LocationId locationId);

    List<MonsterInfo> getMonsters();

    boolean isEpicMonsterPresent(EpicMonsterId epicMonsterId);

    List<AbstractMonsterInfo> getMonstersInCup();


    boolean canSpawn(NonEpicMonsterId monsterId);
}
