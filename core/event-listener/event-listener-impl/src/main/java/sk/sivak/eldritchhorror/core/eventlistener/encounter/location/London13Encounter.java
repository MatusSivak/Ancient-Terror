package sk.sivak.eldritchhorror.core.eventlistener.encounter.location;

import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.constants.location.LocationInfo;
import sk.sivak.eldritchhorror.core.constants.location.LocationType;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestPassFlavorInfoFailFlavorInfoTemplate;
import sk.sivak.eldritchhorror.core.eventtype.data.basic.SelectLocationData;
import sk.sivak.eldritchhorror.core.eventtype.data.encounter.LocationEncounter;

import java.util.Collections;
import java.util.LinkedList;

public class London13Encounter extends AbstractLocationEncounter{

    public London13Encounter() {
        super(13, LocationEncounter.LocationEncounterType.LONDON);
    }

    @Override
    protected void execute() {
        new TestPassFlavorInfoFailFlavorInfoTemplate(getTextBuilder(), Stat.STRENGTH, -1,
                () -> {
                    ServicePlatform.get().getTokenService().gainClueFromPool();
                    spawnClueOnWildernessSpace();
                },
                () -> ServicePlatform.get().getGameService().gainCondition(ConditionId.POISONED))
                .withTwoPassInfos().execute();
    }

    private void spawnClueOnWildernessSpace() {
        LinkedList<LocationId> wildernessLocations = new LinkedList<>();
        for (LocationId locationId : LocationId.values()) {
            LocationInfo locationInfo = ServicePlatform.get().getLocationMap().getLocationInfo(locationId);
            if (locationInfo.getLocationType() == LocationType.WILDERNESS) {
                wildernessLocations.add(locationId);
            }
        }
        Collections.shuffle(wildernessLocations);
        ServicePlatform.get().getTokenService().spawnClueAt(wildernessLocations.get(0));
    }
}
