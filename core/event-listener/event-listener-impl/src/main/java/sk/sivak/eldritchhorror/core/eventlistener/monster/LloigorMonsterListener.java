package sk.sivak.eldritchhorror.core.eventlistener.monster;

import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.constants.location.LocationInfo;
import sk.sivak.eldritchhorror.core.constants.monster.MonsterInfo;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.model.InvestigatorRead;

import java.util.LinkedList;
import java.util.List;

import static sk.sivak.eldritchhorror.core.eventtype.DirectEvent.RECKONING_MONSTER;

public class LloigorMonsterListener extends AbstractMonsterListener {

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
            LinkedList<LocationId> locations = new LinkedList<>();
            LocationId currentLocation = monsterInfo.getCurrentLocation();
            locations.add(currentLocation);

            List<LocationInfo.Connection> connections = ServicePlatform.get().getLocationMap()
                    .getLocationInfo(currentLocation).getConnections();
            for (LocationInfo.Connection connection : connections) {
                locations.add(connection.getLocationId());
            }

            List<InvestigatorId> affectedInvestigators = new LinkedList<>();

            for (InvestigatorRead investigator : ServicePlatform.get().getInvestigators().getOnBoardInvestigators()) {
                if (!locations.contains(investigator.getCurrentLocationId())) {
                    continue;
                }
                affectedInvestigators.add(investigator.getInfo().getInvestigatorId());
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
                ServicePlatform.get().getTokenService().loseSanity(1);
            }

        }
    }

    @Override
    public void unregister() {
        ServicePlatform.get().getEventQueue().unregisterListener(reckoningListener);
    }
}
