package sk.sivak.eldritchhorror.core.constants.gate;

import sk.sivak.eldritchhorror.core.constants.location.LocationId;

/**
 * @author msivak
 */
public class Gate implements GateInfo {

    private GateColor gateColor;

    private LocationId locationId;

    public Gate(GateColor gateColor, LocationId locationId) {
        this.gateColor = gateColor;
        this.locationId = locationId;
    }

    // Just for the JSON
    public Gate() {
    }

    @Override
    public GateColor getGateColor() {
        return gateColor;
    }

    @Override
    public LocationId getLocationId() {
        return locationId;
    }

    public void setGateColor(GateColor gateColor) {
        this.gateColor = gateColor;
    }

    public void setLocationId(LocationId locationId) {
        this.locationId = locationId;
    }

    @Override
    public String toString() {
        return locationId + "(" + gateColor + ")";
    }
}
