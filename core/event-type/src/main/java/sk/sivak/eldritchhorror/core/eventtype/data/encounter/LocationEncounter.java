package sk.sivak.eldritchhorror.core.eventtype.data.encounter;

import sk.sivak.eldritchhorror.core.constants.encounter.EncounterButtonData;
import sk.sivak.eldritchhorror.core.constants.encounter.EncounterType;

public class LocationEncounter extends Encounter {

    private LocationEncounterType locationEncounterType;

    public LocationEncounter(LocationEncounterType locationEncounterType) {
        super(EncounterType.LOCATION);
        this.locationEncounterType = locationEncounterType;
    }

    public LocationEncounterType getLocationEncounterType() {
        return locationEncounterType;
    }

    @Override
    public EncounterButtonData buildButtonData() {
        EncounterButtonData encounterButtonData = super.buildButtonData();
        encounterButtonData.setButtonIcon("encounter/big_city.png");
        encounterButtonData.setFirstLine(locationEncounterType.locationName);
        encounterButtonData.setSecondLine(locationEncounterType.locationEffect);
        return encounterButtonData;
    }

    public enum LocationEncounterType {
        SAN_FRANCISCO("San Francisco","Improve Observation"),
        BUENOS_AIRES("Buenos Aires","Gain Ritual Spells"),
        ARKHAM("Arkham","Gain Incantation Spells"),
        LONDON("London","Spawn Clues"),
        ROME("Rome","Improve Will"),
        ISTANBUL("Istanbul","Improve Influence"),
        SHANGHAI("Shanghai","Improve Lore"),
        TOKYO("Tokyo","Defeat Monsters"),
        SYDNEY("Sydney","Improve Strength");

        private String locationName;
        private String locationEffect;
        LocationEncounterType(String locationName, String locationEffect) {
            this.locationName = locationName;
            this.locationEffect = locationEffect;
        }

        public String getLocationName() {
            return locationName;
        }
    }
}
