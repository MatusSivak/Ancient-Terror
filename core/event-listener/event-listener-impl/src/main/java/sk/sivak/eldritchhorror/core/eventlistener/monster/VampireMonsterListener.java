package sk.sivak.eldritchhorror.core.eventlistener.monster;

import sk.sivak.eldritchhorror.core.constants.monster.MonsterInfo;
import sk.sivak.eldritchhorror.core.eventlistener.EventListenerImpl;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.combat.CombatData;

import static sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent.END_OF_COMBAT_EVENT;
import static sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent.HIDE_COMBAT_TABLE;

public class VampireMonsterListener extends AbstractMonsterListener {


    private AfterHorrorCheckListener afterHorrorCheckListener;
    private RestoreHealthListener restoreHealthListener;

    @Override
    public void register(MonsterInfo monsterInfo) {
        super.register(monsterInfo);
        afterHorrorCheckListener = new AfterHorrorCheckListener();
        ServicePlatform.get().getEventQueue().addAfterEventListener(afterHorrorCheckListener, BeforeAfterEvent.AFTER_HORROR_CHECK);
        restoreHealthListener = new RestoreHealthListener();
        ServicePlatform.get().getEventQueue().addAfterEventListener(restoreHealthListener, END_OF_COMBAT_EVENT);
    }

    private class AfterHorrorCheckListener extends EventListenerImpl<CombatData> {

        @Override
        public void onNotify(CombatData eventData) {
            if (eventData.getMonsterInfo() != monsterInfo) {
                return;
            }
            if (eventData.getHorrorTestResult().getScore() != 0) {
                return;
            }
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getMonsterService().highlightSpecialText(monsterInfo);
            ServicePlatform.get().getService().skipBeforeEvent(HIDE_COMBAT_TABLE, null);
            ServicePlatform.get().getService().release();
        }

        @Override
        public Class<CombatData> getDataClass() {
            return CombatData.class;
        }
    }

    private class RestoreHealthListener extends EventListenerImpl<CombatData> {

        @Override
        public void onNotify(CombatData eventData) {
            if (eventData.getMonsterInfo() != monsterInfo) {
                return;
            }
            if (eventData.getHealthLost() == 0) {
                return;
            }
            if (eventData.getMonsterInfo().getCurrentHealth().equals(eventData.getMonsterInfo().getToughness())) {
                return;
            }
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getMonsterService().restoreHealth(monsterInfo, 2, true, false);
            ServicePlatform.get().getService().release();
        }

        @Override
        public Class<CombatData> getDataClass() {
            return CombatData.class;
        }
    }

    @Override
    public void unregister() {
        ServicePlatform.get().getEventQueue().unregisterListener(afterHorrorCheckListener);
        ServicePlatform.get().getEventQueue().unregisterListener(restoreHealthListener);
    }
}
