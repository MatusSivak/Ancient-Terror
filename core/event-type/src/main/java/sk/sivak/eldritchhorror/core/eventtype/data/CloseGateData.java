package sk.sivak.eldritchhorror.core.eventtype.data;

import sk.sivak.eldritchhorror.core.constants.gate.GateColor;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;

public class CloseGateData {

    private LocationId locationId;
    private GateColor gateColor;
    private boolean isOtherworldEncounter;

    public CloseGateData(LocationId locationId, GateColor gateColor, boolean isOtherworldEncounter) {
        this.locationId = locationId;
        this.gateColor = gateColor;
        this.isOtherworldEncounter = isOtherworldEncounter;
    }

    public LocationId getLocationId() {
        return locationId;
    }

    public GateColor getGateColor() {
        return gateColor;
    }

    public boolean isOtherworldEncounter() {
        return isOtherworldEncounter;
    }
}
