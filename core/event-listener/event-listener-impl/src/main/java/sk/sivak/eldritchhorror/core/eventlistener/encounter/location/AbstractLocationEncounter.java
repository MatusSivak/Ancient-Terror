package sk.sivak.eldritchhorror.core.eventlistener.encounter.location;

import sk.sivak.eldritchhorror.core.constants.location.LocationType;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.builder.GeneralEncounterTextBuilder;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.builder.LocationEncounterTextBuilder;
import sk.sivak.eldritchhorror.core.eventtype.data.encounter.LocationEncounter;

public abstract class AbstractLocationEncounter {

    private final LocationEncounterTextBuilder locationEncounterTextBuilder;
    private LocationEncounter.LocationEncounterType locationType;

    protected AbstractLocationEncounter(int page, LocationEncounter.LocationEncounterType locationType) {
        locationEncounterTextBuilder = new LocationEncounterTextBuilder(page, locationType);
        this.locationType = locationType;
    }

    protected LocationEncounterTextBuilder getTextBuilder() {
        return locationEncounterTextBuilder;
    }

    public void executeWhole() {
        ServicePlatform.get().getService().hold();
        ServicePlatform.get().getEncounterService().showTypewriterPaper(true);
        ServicePlatform.get().getEncounterService().typeHeader(locationType.getLocationName());
        ServicePlatform.get().getEncounterService().typeFlavor(locationEncounterTextBuilder.withFlavor().build());
        execute();
        ServicePlatform.get().getService().convertToNull();
        ServicePlatform.get().getService().release();
    }

    protected abstract void execute();


}
