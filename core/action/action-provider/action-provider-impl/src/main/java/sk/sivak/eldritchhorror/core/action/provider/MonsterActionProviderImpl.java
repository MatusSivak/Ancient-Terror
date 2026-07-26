package sk.sivak.eldritchhorror.core.action.provider;

import sk.sivak.eldritchhorror.core.action.Action;
import sk.sivak.eldritchhorror.core.action.HookableAction;
import sk.sivak.eldritchhorror.core.action.impl.monster.DealDamageToMonsterAction;
import sk.sivak.eldritchhorror.core.action.impl.monster.DefeatMonsterAction;
import sk.sivak.eldritchhorror.core.action.impl.monster.DiscardMonsterAction;
import sk.sivak.eldritchhorror.core.action.impl.monster.HighlightReckoningTextAction;
import sk.sivak.eldritchhorror.core.action.impl.monster.HighlightSpawnTextAction;
import sk.sivak.eldritchhorror.core.action.impl.monster.HighlightSpecialTextAction;
import sk.sivak.eldritchhorror.core.action.impl.monster.MoveMonsterAction;
import sk.sivak.eldritchhorror.core.action.impl.monster.RestoreHealthAction;
import sk.sivak.eldritchhorror.core.action.impl.monster.SelectSingleMonsterAction;
import sk.sivak.eldritchhorror.core.constants.monster.MonsterId;
import sk.sivak.eldritchhorror.core.constants.monster.MonsterInfo;
import sk.sivak.eldritchhorror.core.eventtype.SelectMonsterData;
import sk.sivak.eldritchhorror.core.eventtype.data.combat.CombatData;
import sk.sivak.eldritchhorror.core.eventtype.data.monster.DealDamageToMonsterData;
import sk.sivak.eldritchhorror.core.eventtype.data.monster.DefeatMonsterData;
import sk.sivak.eldritchhorror.core.eventtype.data.monster.MoveMonsterData;

public class MonsterActionProviderImpl implements MonsterActionProvider {

    @Override
    public HookableAction<DealDamageToMonsterData, DealDamageToMonsterData> dealDamageToMonster() {
        return new DealDamageToMonsterAction();
    }

    @Override
    public HookableAction<DefeatMonsterData, DefeatMonsterData> defeatMonster() {
        return new DefeatMonsterAction();
    }

    @Override
    public HookableAction<MonsterInfo, MonsterInfo> discardMonster() {
        return new DiscardMonsterAction();
    }

    @Override
    public HookableAction<SelectMonsterData, MonsterInfo> selectSingleMonster() {
        return new SelectSingleMonsterAction();
    }

    @Override
    public Action<Object, Void> highlightSpecialText(MonsterInfo monsterInfo) {
        return new HighlightSpecialTextAction(monsterInfo);
    }

    @Override
    public Action<Object, Void> highlightReckoningText(MonsterInfo monsterInfo) {
        return new HighlightReckoningTextAction(monsterInfo);
    }

    @Override
    public Action<Object, Void> highlightSpawnText(MonsterInfo monsterInfo) {
        return new HighlightSpawnTextAction(monsterInfo);
    }

    @Override
    public Action<Object, MonsterInfo> findAmbushingMonster(MonsterId monsterId) {
        return new FindAmbushingMonsterAction(monsterId);
    }

    @Override
    public Action<Object, MonsterInfo> returnAmbushingMonster(MonsterInfo monsterInfo) {
        return new ReturnAmbushingMonsterAction(monsterInfo);
    }

    @Override
    public HookableAction<MoveMonsterData, MoveMonsterData> moveMonster() {
        return new MoveMonsterAction();
    }

    @Override
    public Action<Object, Void> restoreHealth(MonsterInfo monsterInfo, int amount, boolean highlightSpecialText, boolean highlightReckoningText) {
        return new RestoreHealthAction(monsterInfo, amount, highlightSpecialText, highlightReckoningText);
    }
}
