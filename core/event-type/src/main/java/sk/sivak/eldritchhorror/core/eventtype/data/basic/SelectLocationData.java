package sk.sivak.eldritchhorror.core.eventtype.data.basic;

import sk.sivak.eldritchhorror.core.constants.location.LocationId;

import java.util.List;

public class SelectLocationData {
    private List<LocationId> locations;

    public SelectLocationData(List<LocationId> locations) {
        this.locations = locations;
    }

    public List<LocationId> getLocations() {
        return locations;
    }
}
