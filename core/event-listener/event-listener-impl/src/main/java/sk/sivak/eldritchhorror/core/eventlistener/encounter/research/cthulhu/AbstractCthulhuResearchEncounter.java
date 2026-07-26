package sk.sivak.eldritchhorror.core.eventlistener.encounter.research.cthulhu;

import java8.features.stream.Stream;
import sk.sivak.eldritchhorror.core.constants.ancientone.AncientOneId;
import sk.sivak.eldritchhorror.core.constants.clue.ClueInfo;
import sk.sivak.eldritchhorror.core.constants.location.FindNearestData;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.constants.location.LocationInfo;
import sk.sivak.eldritchhorror.core.constants.location.LocationType;
import sk.sivak.eldritchhorror.core.constants.location.PathType;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.research.AbstractResearchEncounter;
import sk.sivak.eldritchhorror.core.eventtype.data.basic.SelectLocationData;

import java.util.List;

public abstract class AbstractCthulhuResearchEncounter extends AbstractResearchEncounter {

    public AbstractCthulhuResearchEncounter(int page, LocationType locationType) {
        super(page, locationType, AncientOneId.CTHULHU);
    }

    protected void moveClueToSea() {
        ClueInfo clueOnThisSpace = Stream.findFirstOrException(ServicePlatform.get().getCluePool().getSpawnedClues(), clue -> clue.getCurrentLocationId() == getLocationId());
        FindNearestData nearestSeaSpace = ServicePlatform.get().getLocationMap().findNearest(getLocationId(),
                locationInfo -> locationInfo.getLocationType() == LocationType.SEA);
        ServicePlatform.get().getTokenService().moveClue(clueOnThisSpace.getSpawnLocationId(), nearestSeaSpace.getLocationId());
    }

    protected void moveToTheNearestSea() {
        LocationId currentLocationId = ServicePlatform.get().getInvestigators().getActiveInvestigator().getCurrentLocationId();
        FindNearestData nearestSea = ServicePlatform.get().getLocationMap().findNearest(currentLocationId,
                locationInfo -> locationInfo.getLocationType() == LocationType.SEA);

        ServicePlatform.get().getBasicActionService().travelToLocation(new LocationInfo.Connection() {
            @Override
            public LocationId getLocationId() {
                return nearestSea.getLocationId();
            }

            @Override
            public PathType getPathType() {
                return PathType.WALK;
            }
        });
    }

    protected void discardOneVortex() {
        List<LocationId> spawnedVortexes = ServicePlatform.get().getVortexes().getSpawnedVortexes();
        if (spawnedVortexes.isEmpty()) {
            return;
        }
        SelectLocationData selectLocationData = new SelectLocationData(spawnedVortexes);
        ServicePlatform.get().getGameService().selectLocation(selectLocationData).subscribe(locationId -> {
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getService().moveCameraToLocation(locationId);
            ServicePlatform.get().getGameService().removeVortex(locationId);
            ServicePlatform.get().getService().release();
        });
    }
}
