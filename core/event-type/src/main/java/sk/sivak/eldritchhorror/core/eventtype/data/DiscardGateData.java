package sk.sivak.eldritchhorror.core.eventtype.data;

import sk.sivak.eldritchhorror.core.constants.gate.GateColor;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;

public class DiscardGateData {

    private LocationId locationId;
    private GateColor gateColor;

    public DiscardGateData(LocationId locationId, GateColor gateColor) {
        this.locationId = locationId;
        this.gateColor = gateColor;
    }

    public LocationId getLocationId() {
        return locationId;
    }

    public GateColor getGateColor() {
        return gateColor;
    }
}
