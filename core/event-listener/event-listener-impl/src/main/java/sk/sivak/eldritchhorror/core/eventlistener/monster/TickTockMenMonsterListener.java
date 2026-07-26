package sk.sivak.eldritchhorror.core.eventlistener.monster;

import sk.sivak.eldritchhorror.core.constants.monster.AbstractMonsterInfo;
import sk.sivak.eldritchhorror.core.constants.monster.MonsterInfo;
import sk.sivak.eldritchhorror.core.constants.monster.epic.EpicMonsterId;
import sk.sivak.eldritchhorror.core.constants.monster.epic.TickTockMenMonster;
import sk.sivak.eldritchhorror.core.constants.question.Answer;
import sk.sivak.eldritchhorror.core.constants.question.Question;
import sk.sivak.eldritchhorror.core.eventlistener.EventListenerImpl;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.eventtype.DirectEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.combat.CombatData;
import sk.sivak.eldritchhorror.core.eventtype.data.test.RerollData;
import sk.sivak.eldritchhorror.core.eventtype.data.test.RerollUsingData;

import static sk.sivak.eldritchhorror.core.constants.test.TestFlavorType.COMBAT;
import static sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent.REROLL_USING_CLUE;

public class TickTockMenMonsterListener extends AbstractMonsterListener {


    private BeforeRerollUsingClue beforeRerollUsingClue;

    @Override
    public void register(MonsterInfo monsterInfo) {
        super.register(monsterInfo);
        ((AbstractMonsterInfo) monsterInfo).setToughness(ServicePlatform.get().getModel().getReferenceCard().getPlayers() + 2);
        ((AbstractMonsterInfo) monsterInfo).setCurrentHealth(monsterInfo.getToughness());

        beforeRerollUsingClue = new BeforeRerollUsingClue();
        ServicePlatform.get().getEventQueue().addBeforeEventListener(beforeRerollUsingClue, REROLL_USING_CLUE);
    }

    @Override
    public void justRegisterListeners(MonsterInfo monsterInfo) {
        this.monsterInfo = monsterInfo;
        ((AbstractMonsterInfo) monsterInfo).setToughness(ServicePlatform.get().getModel().getReferenceCard().getPlayers() + 2);

        beforeRerollUsingClue = new BeforeRerollUsingClue();
        ServicePlatform.get().getEventQueue().addBeforeEventListener(beforeRerollUsingClue, REROLL_USING_CLUE);

    }

    @Override
    public void unregister() {
        ServicePlatform.get().getEventQueue().unregisterListener(beforeRerollUsingClue);
    }

    private class BeforeRerollUsingClue extends EventListenerImpl<RerollUsingData> {

        @Override
        public void onNotify(RerollUsingData eventData) {
            if (ServicePlatform.get().getTestFlavor().getFlavorType() != COMBAT) {
                return;
            }
            MonsterInfo monsterInfo = ((CombatData) ServicePlatform.get().getTestFlavor().getFlavorData()).getMonsterInfo();
            if (monsterInfo.getMonsterId() != EpicMonsterId.TICK_TOCK_MEN) {
                return;
            }
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getMonsterService().highlightSpecialText(monsterInfo);
            RerollData rerollData = new RerollData();
            rerollData.setExecuted(false);
            rerollData.setTestData(eventData.getTestData());
            ServicePlatform.get().getService().skipAfterEvent(BeforeAfterEvent.REROLL_USING_CLUE,  rerollData);
            ServicePlatform.get().getService().release();
        }

        @Override
        public Class<RerollUsingData> getDataClass() {
            return RerollUsingData.class;
        }
    }
}
