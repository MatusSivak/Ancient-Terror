package sk.sivak.eldritchhorror.core.constants.location;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static java8.features.stream.Stream.collectToList;

/**
 * @author msivak
 */
public class Location implements LocationInfo {

    private LocationId locationId;
    private LocationType locationType;
    private List<Connection> connections = new LinkedList<>();

    public Location(LocationId locationId, LocationType locationType, Connection... connections) {
        this.locationId = locationId;
        this.locationType = locationType;
        Collections.addAll(this.connections, connections);
    }

    @Override
    public LocationId getLocationId() {
        return locationId;
    }

    @Override
    public LocationType getLocationType() {
        return locationType;
    }

    public List<Connection> getConnections() {
        return connections;
    }


    public List<Connection> getTrainConnections() {
        return getPathTypeConnections(PathType.TRAIN);
    }

    public List<Connection> getShipConnections() {
        return getPathTypeConnections(PathType.SHIP);
    }

    private List<Connection> getPathTypeConnections(PathType pathType) {
        return collectToList(connections, connection -> pathType == connection.getPathType());
    }


}
