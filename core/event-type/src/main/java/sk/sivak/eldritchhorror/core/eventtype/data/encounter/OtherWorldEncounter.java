package sk.sivak.eldritchhorror.core.eventtype.data.encounter;

import sk.sivak.eldritchhorror.core.constants.encounter.EncounterButtonData;
import sk.sivak.eldritchhorror.core.constants.encounter.EncounterType;
import sk.sivak.eldritchhorror.core.constants.gate.GateColor;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;

public class OtherWorldEncounter extends Encounter {

    private GateColor gateColor;
    private final LocationId locationId;
    private boolean hideEncounterTable = true;
    public OtherWorldEncounter(GateColor gateColor, LocationId locationId) {
        super(EncounterType.OTHER_WORLD);
        this.gateColor = gateColor;
        this.locationId = locationId;
    }

    public GateColor getGateColor() {
        return gateColor;
    }

    public LocationId getLocationId() {
        return locationId;
    }

    public void setHideEncounterTable(boolean hideEncounterTable) {
        this.hideEncounterTable = hideEncounterTable;
    }

    public boolean isHideEncounterTable() {
        return hideEncounterTable;
    }

    @Override
    public EncounterButtonData buildButtonData() {
        EncounterButtonData encounterButtonData = super.buildButtonData();
        switch (gateColor) {
            case RED:
                encounterButtonData.setButtonIcon("RED_GATE");
                break;
            case GREEN:
                encounterButtonData.setButtonIcon("GREEN_GATE");
                break;
            case BLUE:
                encounterButtonData.setButtonIcon("BLUE_GATE");
                break;
        }
        encounterButtonData.setFirstLine("Other World");
        return encounterButtonData;
    }
}
