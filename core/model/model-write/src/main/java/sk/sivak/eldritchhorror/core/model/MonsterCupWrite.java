package sk.sivak.eldritchhorror.core.model;

import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.constants.monster.AbstractMonsterInfo;
import sk.sivak.eldritchhorror.core.constants.monster.MonsterId;
import sk.sivak.eldritchhorror.core.constants.monster.MonsterInfo;
import sk.sivak.eldritchhorror.core.constants.monster.NonEpicMonsterId;
import sk.sivak.eldritchhorror.core.constants.monster.epic.EpicMonsterId;
import sk.sivak.eldritchhorror.core.model.save.MonsterCupSaveDataRead;
import sk.sivak.eldritchhorror.core.model.save.SaveDataWrite;

import java.util.List;

/**
 * @author msivak
 */
public interface MonsterCupWrite extends MonsterCupRead {

    void createMonsterCup();

    MonsterInfo spawnMonster(LocationId locationId, boolean overrideSpawnLocation);

    MonsterInfo spawnMonster(MonsterId monsterId, LocationId locationId, boolean overrideSpawnLocation);

    void loseHealth(MonsterInfo monsterInfo, int amount);

    void discardMonster(MonsterInfo monsterInfo);

    void defeatMonster(MonsterInfo monsterInfo);

    MonsterInfo initAmbushingMonster(MonsterId monsterId, LocationId locationId);

    void returnAmbushingMonster(MonsterInfo monsterInfo);

    void updateMonsterLocation(MonsterInfo monsterInfo, LocationId targetLocationId);

    SaveDataWrite.MonsterCupSaveDataWrite save();

    void load(MonsterCupSaveDataRead monsterCupSaveData);

    void removeFromCup(List<MonsterId> removedMonsters);
}
