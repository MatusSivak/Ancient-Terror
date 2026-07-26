package sk.sivak.eldritchhorror.core.eventlistener.monster;

import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.monster.AbstractMonsterInfo;
import sk.sivak.eldritchhorror.core.constants.monster.MonsterInfo;
import sk.sivak.eldritchhorror.core.constants.question.Answer;
import sk.sivak.eldritchhorror.core.constants.question.Question;
import sk.sivak.eldritchhorror.core.eventlistener.EventListenerImpl;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.eventtype.DirectEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.combat.CombatData;

public class CthulhuCultistMonsterListener extends AbstractMonsterListener {


    private BeforeDamageTestListener beforeDamageTestListener;

    private int sanityDamage = 1;
    private AfterAdvanceDoomListener afterAdvanceDoomListener;

    @Override
    public void register(MonsterInfo monsterInfo) {
        super.register(monsterInfo);
        AbstractMonsterInfo editableMonsterInfo = (AbstractMonsterInfo) monsterInfo;
        editableMonsterInfo.setDamage(1);
        editableMonsterInfo.setDamageTestType(Stat.STRENGTH);
        editableMonsterInfo.setHorror(null);
        editableMonsterInfo.setToughness(1);
        editableMonsterInfo.setCurrentHealth(monsterInfo.getToughness());
        editableMonsterInfo.setSpecialText("Before resolving the Strength test, lose 1 Sanity.");

        beforeDamageTestListener = new BeforeDamageTestListener();
        ServicePlatform.get().getEventQueue().addDirectEventListener(beforeDamageTestListener, DirectEvent.BEFORE_DAMAGE_TEST);

        afterAdvanceDoomListener = new AfterAdvanceDoomListener();
        ServicePlatform.get().getEventQueue().addAfterEventListener(afterAdvanceDoomListener, BeforeAfterEvent.ADVANCE_DOOM);
    }

    private class BeforeDamageTestListener extends EventListenerImpl<CombatData> {
        @Override
        public void onNotify(CombatData eventData) {
            if (eventData.getMonsterInfo() != monsterInfo) {
                return;
            }
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getMonsterService().highlightSpecialText(monsterInfo);
            ServicePlatform.get().getTokenService().loseSanity(sanityDamage);
            ServicePlatform.get().getService().convertTo(CombatData.class, () -> eventData);
            ServicePlatform.get().getService().release();
        }

        @Override
        public Class<CombatData> getDataClass() {
            return CombatData.class;
        }
    }

    private class AfterAdvanceDoomListener extends EventListenerImpl<Void> {

        @Override
        public void onNotify(Void eventData) {
            if (ServicePlatform.get().getDoomTrack().getCurrentDoom() > 0) {
                return;
            }
            ((AbstractMonsterInfo) monsterInfo).setToughness(2);
            ((AbstractMonsterInfo) monsterInfo).setCurrentHealth(2);
            ((AbstractMonsterInfo) monsterInfo).setSpecialText("Before resolving the Strength test, lose 2 Sanity.");
            sanityDamage = 2;

            ServicePlatform.get().getService().addEventCommand(in -> {
                ServicePlatform.get().getEventQueue().unregisterListener(afterAdvanceDoomListener);
            });


        }

        @Override
        public Class<Void> getDataClass() {
            return Void.class;
        }
    }

    @Override
    public void unregister() {
        ServicePlatform.get().getEventQueue().unregisterListener(beforeDamageTestListener);
        ServicePlatform.get().getEventQueue().unregisterListener(afterAdvanceDoomListener);
    }
}
