package sk.sivak.eldritchhorror.core.model;

import java8.features.function.Predicate;
import sk.sivak.eldritchhorror.core.constants.location.FindNearestData;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.constants.location.LocationInfo;

/**
 * @author msivak
 */
public interface LocationMapRead {

    LocationInfo getLocationInfo(LocationId locationId);

    FindNearestData findNearest(LocationId startingLocation, Predicate<LocationInfo> predicate);

}
