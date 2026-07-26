package sk.sivak.eldritchhorror.core.eventlistener.monster;

import sk.sivak.eldritchhorror.core.constants.monster.MonsterInfo;
import sk.sivak.eldritchhorror.core.eventlistener.EventListenerImpl;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.combat.CombatData;
import sk.sivak.eldritchhorror.core.eventtype.data.test.RollData;

public class GhostMonsterListener extends AbstractMonsterListener {

    private AfterHorrorCheckListener afterHorrorCheckListener;

    @Override
    public void register(MonsterInfo monsterInfo) {
        super.register(monsterInfo);
        afterHorrorCheckListener = new AfterHorrorCheckListener();
        ServicePlatform.get().getEventQueue().addAfterEventListener(afterHorrorCheckListener, BeforeAfterEvent.AFTER_HORROR_CHECK);
    }

    private class AfterHorrorCheckListener extends EventListenerImpl<CombatData> {

        public AfterHorrorCheckListener() {
            super();
        }


        @Override
        public void onNotify(CombatData eventData) {
            if (eventData.getMonsterInfo() != monsterInfo) {
                return;
            }
            if (eventData.getHorrorTestResult().isSuccessful()) {
                ServicePlatform.get().getService().hold();
                ServicePlatform.get().getService().hideBackground();
                ServicePlatform.get().getMonsterService().dealDamageToMonsterInCombat(monsterInfo, eventData.getHorrorTestResult().getScore(), true);
                ServicePlatform.get().getService().convertTo(CombatData.class, () -> eventData);
                ServicePlatform.get().getService().release();
            } else {
                ServicePlatform.get().getService().hold();
                ServicePlatform.get().getMonsterService().highlightSpecialText(monsterInfo);
                ServicePlatform.get().getService().convertTo(CombatData.class, () -> eventData);
                ServicePlatform.get().getService().release();
            }
        }


        @Override
        public Class<CombatData> getDataClass() {
            return CombatData.class;
        }
    }

    @Override
    public void unregister() {
        ServicePlatform.get().getEventQueue().unregisterListener(afterHorrorCheckListener);
    }
}
