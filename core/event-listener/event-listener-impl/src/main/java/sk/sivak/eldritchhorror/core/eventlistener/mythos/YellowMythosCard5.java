package sk.sivak.eldritchhorror.core.eventlistener.mythos;

import java8.features.stream.Stream;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.constants.location.LocationInfo;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventtype.data.investigator.DelayedData;
import sk.sivak.eldritchhorror.core.model.InvestigatorRead;

import java.util.Collection;
import java.util.List;

public class YellowMythosCard5 implements MythosCardEventListener{

    @Override
    public void execute() {
        LocationId expeditionLocationId = ServicePlatform.get().getExpeditionDeck().getExpeditionTokenLocation();
        LocationInfo locationInfo = ServicePlatform.get().getLocationMap().getLocationInfo(expeditionLocationId);
        Collection<LocationId> locations = Stream.map(locationInfo.getConnections(), LocationInfo.Connection::getLocationId);
        locations.add(expeditionLocationId);
        List<? extends InvestigatorRead> allInvestigators = ServicePlatform.get().getInvestigators().getOnBoardInvestigators();
        List<? extends InvestigatorRead> relevantInvestigators = Stream.collectToList(allInvestigators,
                investigator -> locations.contains(investigator.getCurrentLocationId()));

        ServicePlatform.get().getService().hold();
        ServicePlatform.get().getService().moveCameraToLocation(expeditionLocationId);
        ServicePlatform.get().getService().destroyCurrentExpeditionLocation();
        for (InvestigatorRead investigator : relevantInvestigators) {
            ServicePlatform.get().getInvestigatorService().changeActiveInvestigator(investigator.getInfo().getInvestigatorId());
            ServicePlatform.get().getInvestigatorService().showActiveInvestigator(true);
            ServicePlatform.get().getTokenService().loseHealth(2);
            ServicePlatform.get().getInvestigatorService().becomeDelayed(new DelayedData(investigator.getInfo().getInvestigatorId(), true));
        }
        ServicePlatform.get().getGameService().showExpeditionLocation().subscribe();
        ServicePlatform.get().getService().release();

    }
}
