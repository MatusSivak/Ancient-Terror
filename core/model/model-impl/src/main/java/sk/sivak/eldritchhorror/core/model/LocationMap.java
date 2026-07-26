package sk.sivak.eldritchhorror.core.model;

import java8.features.function.Predicate;
import sk.sivak.eldritchhorror.core.constants.location.FindNearestData;
import sk.sivak.eldritchhorror.core.constants.location.Location;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.constants.location.LocationInfo;
import sk.sivak.eldritchhorror.core.model.util.LocationHelper;
import sk.sivak.eldritchhorror.core.model.util.LocationUtils;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * @author msivak
 */
public class LocationMap implements LocationMapWrite {

    private Map<LocationId, Location> locations;

    @Override
    public void initLocations() {
        locations = new HashMap<>();
        new LocationHelper().initLocations(locations);
    }

    @Override
    public LocationInfo getLocationInfo(LocationId locationId) {
        if (locationId == null) {
            return new Location(null, null);
        }
        return locations.get(locationId);
    }

    @Override
    public FindNearestData findNearest(LocationId startingLocation, Predicate<LocationInfo> predicate) {
        return LocationUtils.findNearest(this, startingLocation, predicate);
    }

}
