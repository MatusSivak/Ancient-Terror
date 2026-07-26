package sk.sivak.eldritchhorror.core.eventlistener.monster;

import sk.sivak.eldritchhorror.core.constants.monster.AbstractMonsterInfo;
import sk.sivak.eldritchhorror.core.constants.monster.MonsterInfo;
import sk.sivak.eldritchhorror.core.constants.monster.epic.EpicMonsterId;
import sk.sivak.eldritchhorror.core.eventlistener.EventListenerImpl;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.combat.CombatData;
import sk.sivak.eldritchhorror.core.eventtype.data.monster.DealDamageToMonsterData;
import sk.sivak.eldritchhorror.core.eventtype.data.monster.DefeatMonsterData;

public class ShubNiggurathMonsterListener extends AbstractMonsterListener {

    private BeforeDealDamageToMonsterListener beforeDealDamageToMonsterListener;
    private BeforeDestroyMonsterHealthListener beforeDestroyMonsterHealthListener;
    private BeforeAdvanceActiveMysteryListener beforeAdvanceActiveMysteryListener;
    private AfterAdvanceActiveMysteryListener afterAdvanceActiveMysteryListener;
    private AfterResolveCurrentMysteryListener afterResolveCurrentMysteryListener;

    @Override
    public void register(MonsterInfo monsterInfo) {
        super.register(monsterInfo);
        ((AbstractMonsterInfo) monsterInfo).setToughness(ServicePlatform.get().getModel().getReferenceCard().getPlayers() + 3);
        ((AbstractMonsterInfo) monsterInfo).setCurrentHealth(monsterInfo.getToughness());

        registerListeners();

    }

    @Override
    public void justRegisterListeners(MonsterInfo monsterInfo) {
        this.monsterInfo = monsterInfo;
        ((AbstractMonsterInfo) monsterInfo).setToughness(ServicePlatform.get().getModel().getReferenceCard().getPlayers() + 3);

        registerListeners();

        if (ServicePlatform.get().getMysteryDeck().getSolvedMysteriesCount() >= 3) {
            makeHimVulnerable((AbstractMonsterInfo) monsterInfo);
        }
    }


    @Override
    public void unregister() {
    }

    private void registerListeners() {
        AfterDefeatMonsterListener afterDefeatMonsterListener = new AfterDefeatMonsterListener();
        ServicePlatform.get().getEventQueue().addAfterEventListener(afterDefeatMonsterListener, BeforeAfterEvent.DEFEAT_MONSTER);

        beforeDealDamageToMonsterListener = new BeforeDealDamageToMonsterListener();
        ServicePlatform.get().getEventQueue().addBeforeEventListener(beforeDealDamageToMonsterListener, BeforeAfterEvent.DEAL_DAMAGE_TO_MONSTER);

        beforeDestroyMonsterHealthListener = new BeforeDestroyMonsterHealthListener();
        ServicePlatform.get().getEventQueue().addBeforeEventListener(beforeDestroyMonsterHealthListener, BeforeAfterEvent.DESTROY_MONSTER_HEALTH);

        beforeAdvanceActiveMysteryListener = new BeforeAdvanceActiveMysteryListener();
        ServicePlatform.get().getEventQueue().addBeforeEventListener(beforeAdvanceActiveMysteryListener, BeforeAfterEvent.ADVANCE_ACTIVE_MYSTERY);

        afterAdvanceActiveMysteryListener = new AfterAdvanceActiveMysteryListener();
        ServicePlatform.get().getEventQueue().addAfterEventListener(afterAdvanceActiveMysteryListener, BeforeAfterEvent.ADVANCE_ACTIVE_MYSTERY);

        afterResolveCurrentMysteryListener = new AfterResolveCurrentMysteryListener();
        ServicePlatform.get().getEventQueue().addAfterEventListener(afterResolveCurrentMysteryListener, BeforeAfterEvent.RESOLVE_CURRENT_MYSTERY);
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

    private class BeforeDealDamageToMonsterListener extends EventListenerImpl<DealDamageToMonsterData> {

        @Override
        public void onNotify(DealDamageToMonsterData eventData) {
            if (eventData.getMonsterInfo().getMonsterId() != EpicMonsterId.SHUB_NIGGURATH) {
                return;
            }
            if (ServicePlatform.get().getMysteryDeck().getSolvedMysteriesCount() == 3) {
                return;
            }
            if (eventData.getAmount() == 0) {
                return;
            }

            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getMonsterService().highlightSpecialText(monsterInfo);
            eventData.setAmount(0);
            ServicePlatform.get().getService().convertTo(DealDamageToMonsterData.class, () -> eventData);
            ServicePlatform.get().getService().release();
        }

        @Override
        public Class<DealDamageToMonsterData> getDataClass() {
            return DealDamageToMonsterData.class;
        }
    }

    private class BeforeDestroyMonsterHealthListener extends EventListenerImpl<CombatData> {

        @Override
        public void onNotify(CombatData eventData) {
            if (eventData.getMonsterInfo().getMonsterId() != EpicMonsterId.SHUB_NIGGURATH) {
                return;
            }
            if (ServicePlatform.get().getMysteryDeck().getSolvedMysteriesCount() == 3) {
                return;
            }
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getService().skipAfterEvent(BeforeAfterEvent.DESTROY_MONSTER_HEALTH, eventData);
            ServicePlatform.get().getService().release();
        }

        @Override
        public Class<CombatData> getDataClass() {
            return CombatData.class;
        }
    }

    private class BeforeAdvanceActiveMysteryListener extends EventListenerImpl<Object> {

        @Override
        public void onNotify(Object eventData) {
            ServicePlatform.get().getEventQueue().unregisterListener(beforeDealDamageToMonsterListener);
        }

        @Override
        public Class<Object> getDataClass() {
            return Object.class;
        }
    }

    private class AfterAdvanceActiveMysteryListener extends EventListenerImpl<Void> {

        @Override
        public void onNotify(Void eventData) {
            ServicePlatform.get().getEventQueue().addBeforeEventListener(beforeDealDamageToMonsterListener, BeforeAfterEvent.DEAL_DAMAGE_TO_MONSTER);
        }

        @Override
        public Class<Void> getDataClass() {
            return Void.class;
        }
    }

    private class AfterResolveCurrentMysteryListener extends EventListenerImpl<Boolean> {

        @Override
        public void onNotify(Boolean eventData) {
            makeHimVulnerable((AbstractMonsterInfo) monsterInfo);
        }

        @Override
        public Class<Boolean> getDataClass() {
            return Boolean.class;
        }
    }

    private void makeHimVulnerable(AbstractMonsterInfo monsterInfo) {
        if (ServicePlatform.get().getMysteryDeck().getSolvedMysteriesCount() >= 3) {
            monsterInfo.setSpecialText(null);
            ServicePlatform.get().getService().addEventCommand(in -> {
                ServicePlatform.get().getEventQueue().unregisterListener(beforeDealDamageToMonsterListener);
                ServicePlatform.get().getEventQueue().unregisterListener(beforeDestroyMonsterHealthListener);
                ServicePlatform.get().getEventQueue().unregisterListener(beforeAdvanceActiveMysteryListener);
                ServicePlatform.get().getEventQueue().unregisterListener(afterAdvanceActiveMysteryListener);
            });
        }
    }
}
