package sk.sivak.eldritchhorror.core.service;

import rx.Single;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.constants.monster.MonsterId;
import sk.sivak.eldritchhorror.core.constants.monster.MonsterInfo;
import sk.sivak.eldritchhorror.core.eventtype.SelectMonsterData;
import sk.sivak.eldritchhorror.core.eventtype.data.combat.CombatData;

/**
 * @author msivak
 */
public interface MonsterService extends CommonService {

    void dealDamageToMonster(MonsterInfo monsterInfo, int amount);

    void dealDamageToMonsterInCombat(MonsterInfo monsterInfo, int amount, boolean displayOrHideMonsterCard);

    void defeatMonster(MonsterInfo monsterInfo, boolean inCombat, boolean destroyCard);

    void discardMonster(MonsterInfo monsterInfo);

    Single<MonsterInfo> selectSingleMonster(SelectMonsterData selectMonsterData);

    void highlightSpecialText(MonsterInfo monsterInfo);

    void highlightReckoningText(MonsterInfo monsterInfo);

    void highlightSpawnText(MonsterInfo monsterInfo);

    Single<CombatData> ambush(MonsterId monsterId);

    void moveMonster(MonsterInfo monsterInfo, LocationId targetLocation);

    void restoreHealth(MonsterInfo monsterInfo, int amount, boolean highlightSpecialText, boolean highlightReckoningText);
}
