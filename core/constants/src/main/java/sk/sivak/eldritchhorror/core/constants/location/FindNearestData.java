package sk.sivak.eldritchhorror.core.constants.location;

import java.util.List;
import java.util.Objects;

public class FindNearestData {
    private LocationId locationId;
    private List<LocationId> previousLocations;
    private int distance;

    public FindNearestData(LocationId locationId, int distance, List<LocationId> previousLocations) {
        this.locationId = locationId;
        this.distance = distance;
        this.previousLocations = previousLocations;
    }

    public LocationId getLocationId() {
        return locationId;
    }

    public List<LocationId> getPreviousLocations() {
        return previousLocations;
    }

    public int getDistance() {
        return distance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FindNearestData that = (FindNearestData) o;
        return locationId == that.locationId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(locationId);
    }

    @Override
    public String toString() {
        return "FindNearestData{" +
                "locationId=" + locationId +
                ", previousLocations=" + previousLocations +
                ", distance=" + distance +
                '}';
    }
}
