package sk.sivak.eldritchhorror.core.eventlistener.encounter.research.shub_niggurath;

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

public abstract class AbstractShubNiggurathResearchEncounter extends AbstractResearchEncounter {

    public AbstractShubNiggurathResearchEncounter(int page, LocationType locationType) {
        super(page, locationType, AncientOneId.SHUB_NIGGURATH);
    }

    protected void moveClueToWilderness() {
        ClueInfo clueOnThisSpace = Stream.findFirstOrException(ServicePlatform.get().getCluePool().getSpawnedClues(), clue -> clue.getCurrentLocationId() == getLocationId());
        FindNearestData nearestWildernessSpace = ServicePlatform.get().getLocationMap().findNearest(getLocationId(),
                locationInfo -> locationInfo.getLocationType() == LocationType.WILDERNESS);
        ServicePlatform.get().getTokenService().moveClue(clueOnThisSpace.getSpawnLocationId(), nearestWildernessSpace.getLocationId());
    }

    protected void moveToTheNearestWilderness() {
        LocationId currentLocationId = ServicePlatform.get().getInvestigators().getActiveInvestigator().getCurrentLocationId();
        FindNearestData nearestWilderness = ServicePlatform.get().getLocationMap().findNearest(currentLocationId,
                locationInfo -> locationInfo.getLocationType() == LocationType.WILDERNESS);

        ServicePlatform.get().getBasicActionService().travelToLocation(new LocationInfo.Connection() {
            @Override
            public LocationId getLocationId() {
                return nearestWilderness.getLocationId();
            }

            @Override
            public PathType getPathType() {
                return PathType.WALK;
            }
        });
    }
}
