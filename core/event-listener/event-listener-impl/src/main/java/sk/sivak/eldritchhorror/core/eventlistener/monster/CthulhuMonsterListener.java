package sk.sivak.eldritchhorror.core.eventlistener.monster;

import sk.sivak.eldritchhorror.core.constants.monster.AbstractMonsterInfo;
import sk.sivak.eldritchhorror.core.constants.monster.MonsterInfo;
import sk.sivak.eldritchhorror.core.eventlistener.EventListenerImpl;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.combat.CombatData;
import sk.sivak.eldritchhorror.core.eventtype.data.monster.DefeatMonsterData;

import static sk.sivak.eldritchhorror.core.eventtype.DirectEvent.SANITY_LOST_IN_HORROR_CHECK;

public class CthulhuMonsterListener extends AbstractMonsterListener {

    private SanityLostInHorrorCheckListener sanityLostInHorrorCheckListener;
    private AfterDefeatMonsterListener afterDefeatMonsterListener;

    @Override
    public void register(MonsterInfo monsterInfo) {
        super.register(monsterInfo);
        ((AbstractMonsterInfo) monsterInfo).setToughness(ServicePlatform.get().getModel().getReferenceCard().getPlayers() + 3);
        ((AbstractMonsterInfo) monsterInfo).setCurrentHealth(monsterInfo.getToughness());

        sanityLostInHorrorCheckListener = new SanityLostInHorrorCheckListener();
        ServicePlatform.get().getEventQueue().addDirectEventListener(sanityLostInHorrorCheckListener, SANITY_LOST_IN_HORROR_CHECK);

        afterDefeatMonsterListener = new AfterDefeatMonsterListener();
        ServicePlatform.get().getEventQueue().addAfterEventListener(afterDefeatMonsterListener, BeforeAfterEvent.DEFEAT_MONSTER);

    }

    @Override
    public void justRegisterListeners(MonsterInfo monsterInfo) {
        this.monsterInfo = monsterInfo;
        ((AbstractMonsterInfo) monsterInfo).setToughness(ServicePlatform.get().getModel().getReferenceCard().getPlayers() + 3);

        sanityLostInHorrorCheckListener = new SanityLostInHorrorCheckListener();
        ServicePlatform.get().getEventQueue().addDirectEventListener(sanityLostInHorrorCheckListener, SANITY_LOST_IN_HORROR_CHECK);

        afterDefeatMonsterListener = new AfterDefeatMonsterListener();
        ServicePlatform.get().getEventQueue().addAfterEventListener(afterDefeatMonsterListener, BeforeAfterEvent.DEFEAT_MONSTER);
    }


    @Override
    public void unregister() {
        /* I will unregister it manually
        ServicePlatform.get().getEventQueue().unregisterListener(sanityLostInHorrorCheckListener);
        ServicePlatform.get().getEventQueue().unregisterListener(afterDefeatMonsterListener);
        */
    }

    private class SanityLostInHorrorCheckListener extends EventListenerImpl<CombatData> {

        @Override
        public void onNotify(CombatData eventData) {
            if (eventData.getSanityLost() == 0) {
                return;
            }
            if (eventData.getMonsterInfo() != monsterInfo) {
                return;
            }
            ServicePlatform.get().getDoomOmenService().increaseAncientOnePower(eventData.getSanityLost());
        }

        @Override
        public Class<CombatData> getDataClass() {
            return CombatData.class;
        }
    }

    private class AfterDefeatMonsterListener extends EventListenerImpl<DefeatMonsterData> {


        @Override
        public void onNotify(DefeatMonsterData eventData) {
            if (eventData.getMonsterInfo() != monsterInfo) {
                return;
            }

            ServicePlatform.get().getDoomOmenService().resolveCurrentMystery();
        }

        @Override
        public Class<DefeatMonsterData> getDataClass() {
            return DefeatMonsterData.class;
        }
    }
}
