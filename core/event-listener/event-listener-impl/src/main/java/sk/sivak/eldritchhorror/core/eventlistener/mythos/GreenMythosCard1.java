package sk.sivak.eldritchhorror.core.eventlistener.mythos;

import java8.features.stream.Stream;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.constants.location.LocationInfo;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.model.InvestigatorRead;

import java.util.Collection;
import java.util.List;

public class GreenMythosCard1 implements MythosCardEventListener{

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
        for (InvestigatorRead investigator : relevantInvestigators) {
            ServicePlatform.get().getInvestigatorService().changeActiveInvestigator(investigator.getInfo().getInvestigatorId());
            ServicePlatform.get().getInvestigatorService().showActiveInvestigator(true);
            ServicePlatform.get().getTokenService().loseHealth(1);
            ServicePlatform.get().getTokenService().loseSanity(1);
        }
        ServicePlatform.get().getService().moveCameraToLocation(expeditionLocationId);
        ServicePlatform.get().getService().discardTopExpedition();
        ServicePlatform.get().getService().release();

    }
}
