package sk.sivak.eldritchhorror.core.eventlistener.monster;

import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.location.FindNearestData;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.constants.monster.MonsterInfo;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.model.InvestigatorRead;

import java.util.List;

import static sk.sivak.eldritchhorror.core.eventtype.DirectEvent.RECKONING_MONSTER;

public class HoundOfTindalosMonsterListener extends AbstractMonsterListener {

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
            FindNearestData nearest = ServicePlatform.get().getLocationMap().findNearest(monsterInfo.getCurrentLocation(), locationInfo -> {
                for (InvestigatorRead investigator : investigators) {
                    if (investigator.getCurrentLocationId() == locationInfo.getLocationId()) {
                        return true;
                    }
                }
                return false;
            });

            if (nearest == null) {
                return;
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

            ServicePlatform.get().getDoomOmenService().onTriggeredReckoning();
            ServicePlatform.get().getService().moveCameraToLocation(monsterInfo.getCurrentLocation());
            ServicePlatform.get().getMonsterService().highlightReckoningText(monsterInfo);
            ServicePlatform.get().getMonsterService().moveMonster(monsterInfo, nearestLocationId);
            ServicePlatform.get().getInvestigatorService().changeActiveInvestigator(nearestInvestigatorId);
            ServicePlatform.get().getInvestigatorService().showActiveInvestigator(false);
            ServicePlatform.get().getTestService().combat(monsterInfo);
        }
    }

    @Override
    public void unregister() {
        ServicePlatform.get().getEventQueue().unregisterListener(reckoningListener);
    }
}
