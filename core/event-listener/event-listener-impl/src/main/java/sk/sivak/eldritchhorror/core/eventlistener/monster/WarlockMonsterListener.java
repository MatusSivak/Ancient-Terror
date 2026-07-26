package sk.sivak.eldritchhorror.core.eventlistener.monster;

import rx.Single;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.location.FindNearestData;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.constants.monster.MonsterInfo;
import sk.sivak.eldritchhorror.core.eventlistener.EventListenerImpl;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.combat.CombatData;
import sk.sivak.eldritchhorror.core.eventtype.data.test.RollData;
import sk.sivak.eldritchhorror.core.model.InvestigatorRead;

import java.util.List;

import static sk.sivak.eldritchhorror.core.eventtype.DirectEvent.RECKONING_MONSTER;

public class WarlockMonsterListener extends AbstractMonsterListener {


    private AfterHorrorCheckListener afterHorrorCheckListener;
    private ReckoningListener reckoningListener;

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
            ServicePlatform.get().getTokenService().loseHealth(1);
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
            List<? extends InvestigatorRead> investigators = ServicePlatform.get().getInvestigators().getOnBoardInvestigators();
            if (investigators.isEmpty()) {
                return;
            }

            ServicePlatform.get().getDoomOmenService().onTriggeredReckoning();
            ServicePlatform.get().getService().moveCameraToLocation(monsterInfo.getCurrentLocation());
            ServicePlatform.get().getMonsterService().highlightReckoningText(monsterInfo);
            RollData rollData = new RollData();
            rollData.setMinSuccessful(3);
            rollData.setMaxFailed(2);
            Single<Integer> singleDieResult = ServicePlatform.get().getTestService().rollDie(rollData);
            singleDieResult.subscribe(this::resolveDiceRoll);
        }

        private void resolveDiceRoll(Integer value) {
            if (value > 2) {
                return;
            }

            List<? extends InvestigatorRead> investigators = ServicePlatform.get().getInvestigators().getOnBoardInvestigators();

            FindNearestData nearest = ServicePlatform.get().getLocationMap().findNearest(monsterInfo.getCurrentLocation(), locationInfo -> {
                for (InvestigatorRead investigator : investigators) {
                    if (investigator.getCurrentLocationId() == locationInfo.getLocationId()) {
                        return true;
                    }
                }
                return false;
            });

            LocationId nearestLocationId = nearest.getLocationId();

            InvestigatorId nearestInvestigatorId = null;
            for (InvestigatorRead investigator : investigators) {
                if (nearestLocationId != investigator.getCurrentLocationId()) {
                    continue;
                }
                nearestInvestigatorId = investigator.getInfo().getInvestigatorId();
                break;
            }

            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getInvestigatorService().changeActiveInvestigator(nearestInvestigatorId);
            ServicePlatform.get().getInvestigatorService().showActiveInvestigator(false);
            ServicePlatform.get().getGameService().gainCondition(ConditionId.CURSED);
            ServicePlatform.get().getService().release();
        }
    }

    @Override
    public void unregister() {
        ServicePlatform.get().getEventQueue().unregisterListener(afterHorrorCheckListener);
        ServicePlatform.get().getEventQueue().unregisterListener(reckoningListener);
    }
}
