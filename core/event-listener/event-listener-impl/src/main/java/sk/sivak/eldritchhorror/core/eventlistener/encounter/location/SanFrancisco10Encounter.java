package sk.sivak.eldritchhorror.core.eventlistener.encounter.location;

import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.constants.location.LocationInfo;
import sk.sivak.eldritchhorror.core.constants.location.PathType;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.SpendSanityTemplate;
import sk.sivak.eldritchhorror.core.eventtype.data.basic.SelectLocationData;
import sk.sivak.eldritchhorror.core.eventtype.data.encounter.LocationEncounter;

public class SanFrancisco10Encounter extends AbstractLocationEncounter{

    public SanFrancisco10Encounter() {
        super(10, LocationEncounter.LocationEncounterType.SAN_FRANCISCO);
    }

    @Override
    protected void execute() {
       new SpendSanityTemplate(getTextBuilder(), 2,this::selectLocationAndMove).execute();
    }

    private void selectLocationAndMove() {
        ServicePlatform.get().getGameService().selectLocation(new SelectLocationData(null)).subscribe(location -> {
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getBasicActionService().travelToLocation(new LocationInfo.Connection() {
                @Override
                public LocationId getLocationId() {
                    return location;
                }

                @Override
                public PathType getPathType() {
                    return PathType.WALK;
                }
            });
            ServicePlatform.get().getService().release();
        });
    }
}
