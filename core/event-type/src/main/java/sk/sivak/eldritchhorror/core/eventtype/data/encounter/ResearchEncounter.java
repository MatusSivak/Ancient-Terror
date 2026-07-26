package sk.sivak.eldritchhorror.core.eventtype.data.encounter;

import sk.sivak.eldritchhorror.core.constants.encounter.EncounterButtonData;
import sk.sivak.eldritchhorror.core.constants.encounter.EncounterType;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.constants.location.LocationType;

public class ResearchEncounter extends Encounter {

    private LocationType locationType;
    private LocationId locationId;


    public ResearchEncounter(LocationType locationType, LocationId locationId) {
        super(EncounterType.RESEARCH);
        this.locationType = locationType;
        this.locationId = locationId;
    }

    public LocationType getLocationType() {
        return locationType;
    }

    public LocationId getLocationId() {
        return locationId;
    }

    @Override
    public EncounterButtonData buildButtonData() {
        EncounterButtonData encounterButtonData = super.buildButtonData();
        encounterButtonData.setFirstLine("Research");
        encounterButtonData.setButtonIcon("token/clue.png");
        return encounterButtonData;
    }
}
