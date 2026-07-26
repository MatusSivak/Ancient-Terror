package sk.sivak.eldritchhorror.core.eventlistener.monster;

import sk.sivak.eldritchhorror.core.constants.monster.AbstractMonsterInfo;
import sk.sivak.eldritchhorror.core.constants.monster.MonsterInfo;
import sk.sivak.eldritchhorror.core.eventlistener.EventListenerImpl;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.combat.CombatData;

public class AzathothCultistMonsterListener extends AbstractMonsterListener {


    private AfterHorrorCheckListener afterHorrorCheckListener;

    @Override
    public void register(MonsterInfo monsterInfo) {
        super.register(monsterInfo);
        AbstractMonsterInfo editableMonsterInfo = (AbstractMonsterInfo) monsterInfo;
        editableMonsterInfo.setDamage(null);
        editableMonsterInfo.setDamageTestType(null);
        editableMonsterInfo.setHorror(1);
        editableMonsterInfo.setToughness(1);
        editableMonsterInfo.setCurrentHealth(monsterInfo.getToughness());
        editableMonsterInfo.setSpecialText("After resolving the Will test, lose 1 Health and defeat this Monster.");

        afterHorrorCheckListener = new AfterHorrorCheckListener();
        ServicePlatform.get().getEventQueue().addAfterEventListener(afterHorrorCheckListener, BeforeAfterEvent.AFTER_HORROR_CHECK);
    }

    private class AfterHorrorCheckListener extends EventListenerImpl<CombatData> {

        @Override
        public void onNotify(CombatData eventData) {
            if (eventData.getMonsterInfo() != monsterInfo) {
                return;
            }
            ServicePlatform.get().getService().startInsertingAfterCommand(BeforeAfterEvent.HIDE_COMBAT_TABLE);
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getMonsterService().highlightSpecialText(monsterInfo);
            ServicePlatform.get().getService().hideBackground();
            ServicePlatform.get().getTokenService().loseHealth(1);
            ServicePlatform.get().getMonsterService().defeatMonster(monsterInfo, true, false);
            ServicePlatform.get().getService().convertTo(Void.class, () -> null);
            ServicePlatform.get().getService().skipBeforeEvent(BeforeAfterEvent.END_OF_COMBAT_EVENT, null);
            ServicePlatform.get().getService().release();
            ServicePlatform.get().getService().endInsertingAtCommand();
        }

        @Override
        public Class<CombatData> getDataClass() {
            return CombatData.class;
        }
    }

    @Override
    public void unregister() {
        ServicePlatform.get().getService().addEventCommand(in -> {
            ServicePlatform.get().getEventQueue().unregisterListener(afterHorrorCheckListener);
        });

    }
}
