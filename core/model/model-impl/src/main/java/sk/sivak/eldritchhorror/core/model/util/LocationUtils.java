package sk.sivak.eldritchhorror.core.model.util;

import java8.features.function.Predicate;
import sk.sivak.eldritchhorror.core.constants.location.FindNearestData;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.constants.location.LocationInfo;
import sk.sivak.eldritchhorror.core.model.LocationMap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class LocationUtils {
    private LocationUtils() {
        throw new UnsupportedOperationException();
    }


    public static FindNearestData findNearest(LocationMap locationMap, LocationId startingLocation, Predicate<LocationInfo> predicate) {
        Queue<FindNearestData> locationsToTest = new LinkedList<>();
        List<LocationId> checkedLocations = new LinkedList<>();
        locationsToTest.add(new FindNearestData(startingLocation, 0, new LinkedList<>()));

        while (!locationsToTest.isEmpty()) {
            FindNearestData currentFindNearestData = locationsToTest.poll();
            LocationInfo locationInfo = locationMap.getLocationInfo(currentFindNearestData.getLocationId());
            if (predicate.test(locationInfo)) {
                return currentFindNearestData;
            } else {
                checkedLocations.add(currentFindNearestData.getLocationId());
                LinkedList<LocationInfo.Connection> connections = new LinkedList<>(locationInfo.getConnections());
                Collections.shuffle(connections);
                for (LocationInfo.Connection connection : connections) {
                    LocationId nextLocationId = connection.getLocationId();
                    ArrayList<LocationId> previousLocations = new ArrayList<>(currentFindNearestData.getPreviousLocations());
                    previousLocations.add(currentFindNearestData.getLocationId());
                    FindNearestData nextFindNearestData =
                            new FindNearestData(nextLocationId, currentFindNearestData.getDistance() + 1, previousLocations);
                    if (checkedLocations.contains(nextLocationId)) {
                        continue;
                    }
                    if (locationsToTest.contains(nextFindNearestData)) {
                        continue;
                    }
                    locationsToTest.add(nextFindNearestData);
                }
            }
        }
        return null;
    }

    public static void main(String[] args) {
        LocationMap locationMap = new LocationMap();
        locationMap.initLocations();
        FindNearestData nearest = findNearest(locationMap, LocationId.SPACE_13, locationInfo -> locationInfo.getLocationId() == LocationId.ANTARCTICA);
        System.out.println(nearest);
    }
}
