package sk.sivak.eldritchhorror.core.eventlistener.encounter.research;

import sk.sivak.eldritchhorror.core.constants.ancientone.AncientOneId;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.constants.location.LocationType;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.builder.ResearchEncounterTextBuilder;
import sk.sivak.eldritchhorror.core.eventtype.data.token.GainClueData;

public abstract class AbstractResearchEncounter {

    private final ResearchEncounterTextBuilder researchEncounterTextBuilder;
    private LocationType locationType;
    private LocationId locationId;

    protected AbstractResearchEncounter(int page, LocationType locationType, AncientOneId ancientOneId) {
        researchEncounterTextBuilder = new ResearchEncounterTextBuilder(page, locationType, ancientOneId);
        this.locationType = locationType;
    }

    protected ResearchEncounterTextBuilder getTextBuilder() {
        return researchEncounterTextBuilder;
    }

    public void executeWhole() {
        ServicePlatform.get().getService().hold();
        ServicePlatform.get().getEncounterService().showTypewriterPaper(true);
        ServicePlatform.get().getEncounterService().typeHeader(locationType.getValue());
        ServicePlatform.get().getEncounterService().typeFlavor(researchEncounterTextBuilder.withFlavor().build());
        execute();
        ServicePlatform.get().getService().convertToNull();
        ServicePlatform.get().getService().release();
    }

    protected abstract void execute();

    public void setLocationId(LocationId locationId) {
        this.locationId = locationId;
    }

    public LocationId getLocationId() {
        return locationId;
    }

    public void gainThisClue() {
        ServicePlatform.get().getService().hold();
        ServicePlatform.get().getService().hideBackground();
        LocationId activeInvestigatorLocationId = ServicePlatform.get().getInvestigators().getActiveInvestigator().getCurrentLocationId();
        if (activeInvestigatorLocationId != locationId) {
            ServicePlatform.get().getService().moveCameraToLocation(locationId);
        }
        GainClueData gainClueData = new GainClueData(true);
        gainClueData.setLocationId(locationId);
        ServicePlatform.get().getTokenService().gainClueFromSpace(gainClueData);
        if (activeInvestigatorLocationId != locationId) {
            ServicePlatform.get().getService().moveCameraToLocation(activeInvestigatorLocationId);
        }
        ServicePlatform.get().getService().release();
    }

    public LocationType getLocationType() {
        return locationType;
    }
}
