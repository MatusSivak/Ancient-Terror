package sk.sivak.eldritchhorror.core.eventlistener.monster;

import rx.Single;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.location.FindNearestData;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.constants.location.LocationInfo;
import sk.sivak.eldritchhorror.core.constants.location.PathType;
import sk.sivak.eldritchhorror.core.constants.monster.MonsterInfo;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventtype.data.test.RollData;
import sk.sivak.eldritchhorror.core.model.InvestigatorRead;

import java.util.List;

import static sk.sivak.eldritchhorror.core.eventtype.DirectEvent.RECKONING_MONSTER;

public class SerpentPeopleMonsterListener extends AbstractMonsterListener {

    private ReckoningListener reckoningListener;

    @Override
    public void register(MonsterInfo monsterInfo) {
        super.register(monsterInfo);
        ServicePlatform.get().getService().hold();
        ServicePlatform.get().getMonsterService().highlightSpawnText(monsterInfo);
        ServicePlatform.get().getMonsterService().moveMonster(monsterInfo, LocationId.THE_AMAZON);
        ServicePlatform.get().getService().release();

        reckoningListener = new ReckoningListener();
        ServicePlatform.get().getEventQueue().addDirectEventListener(reckoningListener, RECKONING_MONSTER);
    }

    @Override
    public void justRegisterListeners(MonsterInfo monsterInfo) {
        this.monsterInfo = monsterInfo;
        reckoningListener = new ReckoningListener();
        ServicePlatform.get().getEventQueue().addDirectEventListener(reckoningListener, RECKONING_MONSTER);
    }

    private class ReckoningListener extends AbstractMonsterReckoningListener {

        @Override
        void onNotify() {
            List<? extends InvestigatorRead> investigators = ServicePlatform.get().getInvestigators().getOnBoardInvestigators();
            if (investigators.isEmpty()) {
                return;
            }

            // show monster
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

            if (nearest.getPreviousLocations().isEmpty()) {
                return; // investigator is already there
            }

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
            ServicePlatform.get().getBasicActionService().travelToLocation(new LocationInfo.Connection() {
                @Override
                public LocationId getLocationId() {
                    return nearest.getPreviousLocations().get(nearest.getPreviousLocations().size()-1);
                }

                @Override
                public PathType getPathType() {
                    return PathType.WALK;
                }
            });
            ServicePlatform.get().getService().release();
        }
    }

    @Override
    public void unregister() {
        ServicePlatform.get().getEventQueue().unregisterListener(reckoningListener);
    }
}
