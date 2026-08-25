package sk.sivak.eldritchhorror.core.eventtype.data.encounter;

import com.badlogic.gdx.math.Vector2;
import sk.sivak.eldritchhorror.core.constants.encounter.EncounterButtonData;
import sk.sivak.eldritchhorror.core.constants.encounter.EncounterType;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;

public class ExpeditionEncounter extends Encounter {

    private LocationId locationId;

    public ExpeditionEncounter(LocationId locationId) {
        super(EncounterType.EXPEDITION);
        this.locationId = locationId;
    }

    public LocationId getLocationId() {
        return locationId;
    }

    @Override
    public EncounterButtonData buildButtonData() {
        EncounterButtonData encounterButtonData = super.buildButtonData();
        encounterButtonData.setButtonIcon("token/compass.png");
        encounterButtonData.setFirstLine("Expedition");
        encounterButtonData.setScaleDownPercentage(1.4f);
        encounterButtonData.setOffset(new Vector2(0,2));

        String secondLine;
        if (locationId == LocationId.THE_AMAZON) {
            secondLine = "Amazon";
        } else if (locationId == LocationId.THE_HEART_OF_AFRICA) {
            secondLine = "Heart of Africa";
        } else if (locationId == LocationId.THE_PYRAMIDS) {
            secondLine = "Pyramids";
        } else if (locationId == LocationId.ANTARCTICA) {
            secondLine = "Antarctica";
        } else if (locationId == LocationId.THE_HIMALAYAS) {
            secondLine = "Himalayas";
        } else if (locationId == LocationId.TUNGUSKA) {
            secondLine = "Tunguska";
        } else {
            throw new IllegalArgumentException("Not a valid expedition: " + locationId);
        }

        encounterButtonData.setSecondLine(secondLine);
        return encounterButtonData;
    }
}
