package sk.sivak.eldritchhorror.core.eventlistener.monster;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.constants.monster.*;
import sk.sivak.eldritchhorror.core.constants.monster.epic.EpicMonsterId;
import sk.sivak.eldritchhorror.core.eventlistener.EventListenerImpl;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.combat.CombatData;
import static sk.sivak.eldritchhorror.core.eventlistener.ancientone.yig.YigEffects.*;
public class YigEpicMonsterListener extends AbstractMonsterListener {
    private EventListenerImpl<CombatData> horror, damage;
    @Override public void register(MonsterInfo info) { setup(info, true); }
    @Override public void justRegisterListeners(MonsterInfo info) { setup(info, false); }
    private void setup(MonsterInfo info, boolean resetHealth) {
        monsterInfo = info;
        AbstractMonsterInfo editable = (AbstractMonsterInfo) info;
        editable.setToughness(p().getModel().getReferenceCard().getPlayers() + (info.getMonsterId() == EpicMonsterId.YIG ? 3 : 2));
        if (resetHealth) editable.setCurrentHealth(info.getToughness());
        horror = new EventListenerImpl<CombatData>() {
            public Class<CombatData> getDataClass() { return CombatData.class; }
            public void onNotify(CombatData data) {
                if (data.getMonsterInfo() != monsterInfo) return;
                sequence(() -> {
                    if (monsterInfo.getMonsterId() == EpicMonsterId.CHILDREN_OF_YIG && data.getHorrorTestResult().getScore() == 0) {
                        p().getMonsterService().moveMonster(monsterInfo, LocationId.getRandomLocations(1).get(0));
                        p().getService().skipBeforeEvent(BeforeAfterEvent.HIDE_COMBAT_TABLE, null);
                    } else if (monsterInfo.getMonsterId() == EpicMonsterId.YIG && data.getSanityLost() > 0) {
                        p().getGameService().gainCondition(ConditionId.CURSED);
                        p().getService().convertTo(CombatData.class, () -> data);
                    }
                });
            }
        };
        damage = new EventListenerImpl<CombatData>() {
            public Class<CombatData> getDataClass() { return CombatData.class; }
            public void onNotify(CombatData data) {
                if (data.getMonsterInfo() == monsterInfo && monsterInfo.getMonsterId() == EpicMonsterId.WINGED_SERPENT && data.getHealthLost() > 0)
                    sequence(() -> { p().getGameService().gainCondition(ConditionId.POISONED); p().getService().convertTo(CombatData.class, () -> data); });
            }
        };
        p().getEventQueue().addAfterEventListener(horror, BeforeAfterEvent.AFTER_HORROR_CHECK);
        p().getEventQueue().addBeforeEventListener(damage, BeforeAfterEvent.DESTROY_MONSTER_HEALTH);
    }
    @Override public void unregister() {
        p().getEventQueue().unregisterListener(horror); p().getEventQueue().unregisterListener(damage);
    }
}
