package sk.sivak.eldritchhorror.core.eventlistener.encounter.general;

import sk.sivak.eldritchhorror.core.constants.location.LocationInfo;
import sk.sivak.eldritchhorror.core.constants.location.LocationType;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.typewriter.TypewriterUtils;
import sk.sivak.eldritchhorror.core.eventtype.data.basic.TravelData;

import java.util.List;
import java.util.Random;

public class Sea2Encounter extends AbstractGeneralEncounter {

    public Sea2Encounter() {
        super(2, LocationType.SEA);
    }

    @Override
    protected void execute() {
        TypewriterUtils.confirmInfos(getTextBuilder().withInfo(1).build(), getTextBuilder().withInfo(2).build()).subscribe(() -> {
            ServicePlatform.get().getEncounterService().hold();
            ServicePlatform.get().getEncounterService().finishTypewriterPaper();
            LocationInfo.Connection connection = selectRandomLocation();
            ServicePlatform.get().getBasicActionService().travelToLocation(connection);
            ServicePlatform.get().getTokenService().loseSanity(1);
            ServicePlatform.get().getEncounterService().release();
        });
    }

    private LocationInfo.Connection selectRandomLocation() {
        LocationInfo locationInfo = ServicePlatform.get().getLocationMap().getLocationInfo(ServicePlatform.get().getInvestigators().getActiveInvestigator().getCurrentLocationId());
        List<LocationInfo.Connection> shipConnections = locationInfo.getShipConnections();
        return shipConnections.get(new Random().nextInt(shipConnections.size()));
    }
}
