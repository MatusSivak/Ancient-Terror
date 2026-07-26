package sk.sivak.eldritchhorror.core.eventtype.data.basic;

import sk.sivak.eldritchhorror.core.constants.location.LocationInfo;

import java.util.List;

public class TravelData {
    private boolean optional;
    private List<LocationInfo.Connection> connectionRestrictions;

    public TravelData(boolean optional) {
        this.optional = optional;
    }

    public List<LocationInfo.Connection> getConnectionRestrictions() {
        return connectionRestrictions;
    }

    public void setConnectionRestrictions(List<LocationInfo.Connection> connectionRestrictions) {
        this.connectionRestrictions = connectionRestrictions;
    }

    public boolean isOptional() {
        return optional;
    }

    public void setOptional(boolean optional) {
        this.optional = optional;
    }
}
