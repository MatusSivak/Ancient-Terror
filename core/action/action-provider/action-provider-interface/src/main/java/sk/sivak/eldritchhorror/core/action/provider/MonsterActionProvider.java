package sk.sivak.eldritchhorror.core.action.provider;

import sk.sivak.eldritchhorror.core.action.Action;
import sk.sivak.eldritchhorror.core.action.HookableAction;
import sk.sivak.eldritchhorror.core.constants.monster.MonsterId;
import sk.sivak.eldritchhorror.core.constants.monster.MonsterInfo;
import sk.sivak.eldritchhorror.core.eventtype.SelectMonsterData;
import sk.sivak.eldritchhorror.core.eventtype.data.monster.DealDamageToMonsterData;
import sk.sivak.eldritchhorror.core.eventtype.data.monster.DefeatMonsterData;
import sk.sivak.eldritchhorror.core.eventtype.data.monster.MoveMonsterData;

public interface MonsterActionProvider {

    HookableAction<DealDamageToMonsterData, DealDamageToMonsterData> dealDamageToMonster();

    HookableAction<DefeatMonsterData, DefeatMonsterData> defeatMonster();

    HookableAction<MonsterInfo, MonsterInfo> discardMonster();

    HookableAction<SelectMonsterData, MonsterInfo> selectSingleMonster();

    Action<Object, Void> highlightSpecialText(MonsterInfo monsterInfo);

    Action<Object, Void> highlightReckoningText(MonsterInfo monsterInfo);

    Action<Object, Void> highlightSpawnText(MonsterInfo monsterInfo);

    Action<Object, MonsterInfo> findAmbushingMonster(MonsterId monsterId);

    Action<Object, MonsterInfo> returnAmbushingMonster(MonsterInfo monsterInfo);

    HookableAction<MoveMonsterData, MoveMonsterData> moveMonster();

    Action<Object, Void> restoreHealth(MonsterInfo monsterInfo, int amount, boolean highlightSpecialText, boolean highlightReckoningText);
}
