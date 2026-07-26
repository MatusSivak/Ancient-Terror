package sk.sivak.eldritchhorror.core.eventlistener.monster;

import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.monster.AbstractMonsterInfo;
import sk.sivak.eldritchhorror.core.constants.monster.MonsterInfo;
import sk.sivak.eldritchhorror.core.eventlistener.EventListenerImpl;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventtype.DirectEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.combat.CombatData;

public class AwakenCthulhuCultistMonsterListener extends AbstractMonsterListener {


    private BeforeDamageTestListener beforeDamageTestListener;

    @Override
    public void register(MonsterInfo monsterInfo) {
        super.register(monsterInfo);
        commonRegister(monsterInfo);
        ((AbstractMonsterInfo)monsterInfo).setCurrentHealth(monsterInfo.getToughness());
    }

    @Override
    public void justRegisterListeners(MonsterInfo monsterInfo) {
        this.monsterInfo = monsterInfo;
        commonRegister(monsterInfo);
    }

    private void commonRegister(MonsterInfo monsterInfo) {
        AbstractMonsterInfo editableMonsterInfo = (AbstractMonsterInfo) monsterInfo;
        editableMonsterInfo.setDamage(1);
        editableMonsterInfo.setDamageTestType(Stat.STRENGTH);
        editableMonsterInfo.setHorror(null);
        editableMonsterInfo.setToughness(2);

        editableMonsterInfo.setSpecialText("Before resolving the Strength test, lose 2 Sanity.");

        beforeDamageTestListener = new BeforeDamageTestListener();
        ServicePlatform.get().getEventQueue().addDirectEventListener(beforeDamageTestListener, DirectEvent.BEFORE_DAMAGE_TEST);
    }

    private class BeforeDamageTestListener extends EventListenerImpl<CombatData> {
        @Override
        public void onNotify(CombatData eventData) {
            if (eventData.getMonsterInfo() != monsterInfo) {
                return;
            }
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getMonsterService().highlightSpecialText(monsterInfo);
            ServicePlatform.get().getTokenService().loseSanity(2);
            ServicePlatform.get().getService().convertTo(CombatData.class, () -> eventData);
            ServicePlatform.get().getService().release();
        }

        @Override
        public Class<CombatData> getDataClass() {
            return CombatData.class;
        }
    }

    @Override
    public void unregister() {
        ServicePlatform.get().getEventQueue().unregisterListener(beforeDamageTestListener);
    }
}
