package sk.sivak.eldritchhorror.core.eventlistener.monster;

import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.location.FindNearestData;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.constants.location.LocationInfo;
import sk.sivak.eldritchhorror.core.constants.monster.MonsterInfo;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventtype.data.investigator.DelayedData;
import sk.sivak.eldritchhorror.core.model.InvestigatorRead;

import java.util.List;
import java.util.Random;

import static sk.sivak.eldritchhorror.core.eventtype.DirectEvent.RECKONING_MONSTER;

public class NightGauntMonsterListener extends AbstractMonsterListener {

    private ReckoningListener reckoningListener;

    @Override
    public void register(MonsterInfo monsterInfo) {
        super.register(monsterInfo);
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

            ServicePlatform.get().getDoomOmenService().onTriggeredReckoning();
            InvestigatorId investigatorOnThisSpace = investigatorOnThisSpace(investigators);
            if (investigatorOnThisSpace != null) {
                // Move monster and investigator one space, he becomes delayed.
                List<LocationInfo.Connection> connections = ServicePlatform.get().getLocationMap().getLocationInfo(monsterInfo.getCurrentLocation()).getConnections();
                LocationId locationId = connections.get(new Random().nextInt(connections.size())).getLocationId();
                ServicePlatform.get().getService().moveCameraToLocation(monsterInfo.getCurrentLocation());
                ServicePlatform.get().getMonsterService().highlightReckoningText(monsterInfo);
                ServicePlatform.get().getInvestigatorService().removeInvestigatorFromPlace(investigatorOnThisSpace);
                ServicePlatform.get().getMonsterService().moveMonster(monsterInfo, locationId);
                ServicePlatform.get().getInvestigatorService().spawnInvestigator(investigatorOnThisSpace, locationId);
                ServicePlatform.get().getInvestigatorService().becomeDelayed(new DelayedData(investigatorOnThisSpace, true));
                return;
            }
            // Move this Monster 2 spaces toward the nearest investigator.

            FindNearestData nearest = ServicePlatform.get().getLocationMap().findNearest(monsterInfo.getCurrentLocation(), locationInfo -> {
                for (InvestigatorRead investigator : investigators) {
                    if (investigator.getCurrentLocationId() == locationInfo.getLocationId()) {
                        return true;
                    }
                }
                return false;
            });

            LocationId targetLocation;
            if (nearest.getDistance() <= 2) {
                targetLocation = nearest.getLocationId();
            } else {
                targetLocation = nearest.getPreviousLocations().get(2);
            }


            ServicePlatform.get().getService().moveCameraToLocation(monsterInfo.getCurrentLocation());
            ServicePlatform.get().getMonsterService().highlightReckoningText(monsterInfo);
            ServicePlatform.get().getMonsterService().moveMonster(monsterInfo, targetLocation);
        }

        private InvestigatorId investigatorOnThisSpace(List<? extends InvestigatorRead> investigators) {
            for (InvestigatorRead investigator : investigators) {
                if (investigator.getCurrentLocationId() == monsterInfo.getCurrentLocation()) {
                    return investigator.getInfo().getInvestigatorId();
                }
            }
            return null;
        }
    }

    @Override
    public void unregister()  {
        ServicePlatform.get().getEventQueue().unregisterListener(reckoningListener);
    }
}
