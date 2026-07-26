package sk.sivak.eldritchhorror.core.eventlistener.monster;

import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.monster.MonsterInfo;
import sk.sivak.eldritchhorror.core.eventlistener.EventListenerImpl;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.combat.CombatData;
import sk.sivak.eldritchhorror.core.model.InvestigatorRead;

import java.util.LinkedList;
import java.util.List;

import static sk.sivak.eldritchhorror.core.eventtype.DirectEvent.RECKONING_MONSTER;

public class WitchMonsterListener extends AbstractMonsterListener {


    private AfterHorrorCheckListener afterHorrorCheckListener;
    private WitchMonsterListener.ReckoningListener reckoningListener;

    @Override
    public void register(MonsterInfo monsterInfo) {
        super.register(monsterInfo);
        afterHorrorCheckListener = new AfterHorrorCheckListener();
        ServicePlatform.get().getEventQueue().addAfterEventListener(afterHorrorCheckListener, BeforeAfterEvent.AFTER_HORROR_CHECK);

        reckoningListener = new ReckoningListener();
        ServicePlatform.get().getEventQueue().addDirectEventListener(reckoningListener, RECKONING_MONSTER);
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
            ServicePlatform.get().getGameService().gainCondition(ConditionId.CURSED);
            ServicePlatform.get().getMonsterService().convertTo(CombatData.class, () -> eventData);
            ServicePlatform.get().getService().release();
        }

        @Override
        public Class<CombatData> getDataClass() {
            return CombatData.class;
        }
    }

    private class ReckoningListener extends AbstractMonsterReckoningListener {

        @Override
        void onNotify() {
            List<InvestigatorId> affectedInvestigators = new LinkedList<>();

            for (InvestigatorRead investigator : ServicePlatform.get().getInvestigators().getOnBoardInvestigators()) {
                boolean hasCursedCondition = ServicePlatform.get().getConditionsDeck().hasCondition(
                        investigator.getInfo().getInvestigatorId(), ConditionId.CURSED);
                if (hasCursedCondition) {
                    affectedInvestigators.add(investigator.getInfo().getInvestigatorId());
                }
            }

            if (affectedInvestigators.isEmpty()) {
                return;
            }

            ServicePlatform.get().getDoomOmenService().onTriggeredReckoning();
            ServicePlatform.get().getService().moveCameraToLocation(monsterInfo.getCurrentLocation());
            ServicePlatform.get().getMonsterService().highlightReckoningText(monsterInfo);
            for (InvestigatorId affectedInvestigator : affectedInvestigators) {
                ServicePlatform.get().getInvestigatorService().changeActiveInvestigator(affectedInvestigator);
                ServicePlatform.get().getInvestigatorService().showActiveInvestigator(false);
                ServicePlatform.get().getTokenService().loseHealth(1);
            }
        }
    }


    @Override
    public void unregister() {
        ServicePlatform.get().getEventQueue().unregisterListener(afterHorrorCheckListener);
        ServicePlatform.get().getEventQueue().unregisterListener(reckoningListener);
    }
}
