package sk.sivak.eldritchhorror.core.eventlistener.monster;

import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.monster.MonsterInfo;
import sk.sivak.eldritchhorror.core.eventlistener.EventListenerImpl;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventtype.data.combat.CombatData;

import static sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent.DESTROY_INVESTIGATOR_HEALTH;
import static sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent.DESTROY_MONSTER_HEALTH;

public class GhoulMonsterListener extends AbstractMonsterListener {


    private AfterDestroyInvestigatorHealth eventListener;

    @Override
    public void register(MonsterInfo monsterInfo) {
        super.register(monsterInfo);
        eventListener = new AfterDestroyInvestigatorHealth();
        ServicePlatform.get().getEventQueue().addBeforeEventListener(eventListener, DESTROY_MONSTER_HEALTH);
    }

    private class AfterDestroyInvestigatorHealth extends EventListenerImpl<CombatData> {

        @Override
        public void onNotify(CombatData eventData) {
            if (eventData.getMonsterInfo() != monsterInfo) {
                return;
            }
            if (eventData.getHealthLost() == 0) {
                return;
            }
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getMonsterService().highlightSpecialText(monsterInfo);
            ServicePlatform.get().getGameService().gainCondition(ConditionId.PARANOIA);
            ServicePlatform.get().getGameService().convertTo(CombatData.class, () -> eventData);
            ServicePlatform.get().getService().release();
        }

        @Override
        public Class<CombatData> getDataClass() {
            return CombatData.class;
        }
    }

    @Override
    public void unregister() {
        ServicePlatform.get().getEventQueue().unregisterListener(eventListener);
    }
}
