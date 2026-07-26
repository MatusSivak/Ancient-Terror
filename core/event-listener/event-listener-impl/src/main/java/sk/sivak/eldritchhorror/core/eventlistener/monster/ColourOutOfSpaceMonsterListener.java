package sk.sivak.eldritchhorror.core.eventlistener.monster;

import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.constants.monster.MonsterInfo;
import sk.sivak.eldritchhorror.core.constants.question.Answer;
import sk.sivak.eldritchhorror.core.constants.question.Question;
import sk.sivak.eldritchhorror.core.eventlistener.EventListenerImpl;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.basic.TravelData;
import sk.sivak.eldritchhorror.core.eventtype.data.combat.CombatData;
import sk.sivak.eldritchhorror.core.eventtype.data.monster.DefeatMonsterData;
import sk.sivak.eldritchhorror.core.eventtype.data.test.RollData;

public class ColourOutOfSpaceMonsterListener extends AbstractMonsterListener {

    private AfterHorrorCheckListener afterHorrorCheckListener;

    @Override
    public void register(MonsterInfo monsterInfo) {
        super.register(monsterInfo);
        ServicePlatform.get().getService().hold();
        ServicePlatform.get().getMonsterService().highlightSpawnText(monsterInfo);
        ServicePlatform.get().getMonsterService().moveMonster(monsterInfo, LocationId.TUNGUSKA);
        ServicePlatform.get().getService().release();

        afterHorrorCheckListener = new AfterHorrorCheckListener();
        ServicePlatform.get().getEventQueue().addAfterEventListener(afterHorrorCheckListener, BeforeAfterEvent.AFTER_HORROR_CHECK);
    }

    @Override
    public void justRegisterListeners(MonsterInfo monsterInfo) {
        this.monsterInfo = monsterInfo;
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
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getMonsterService().highlightSpecialText(monsterInfo);
            RollData rollData = new RollData();
            rollData.setMinSuccessful(5);
            rollData.setMaxFailed(0);
            ServicePlatform.get().getTestService().rollDie(rollData).subscribe(result -> {
                if (result >= 5) {
                    ServicePlatform.get().getService().startInsertingAfterCommand(BeforeAfterEvent.HIDE_COMBAT_TABLE);
                    ServicePlatform.get().getService().hold();
                    ServicePlatform.get().getService().hideBackground();
                    ServicePlatform.get().getMonsterService().defeatMonster(monsterInfo, true, false);
                    ServicePlatform.get().getService().convertTo(Void.class, () -> null);
                    ServicePlatform.get().getService().skipBeforeEvent(BeforeAfterEvent.END_OF_COMBAT_EVENT, null);
                    ServicePlatform.get().getService().release();
                    ServicePlatform.get().getService().endInsertingAtCommand();
                } else {
                    ServicePlatform.get().getService().convertTo(CombatData.class, () ->eventData);
                }
            });
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
    }
}
