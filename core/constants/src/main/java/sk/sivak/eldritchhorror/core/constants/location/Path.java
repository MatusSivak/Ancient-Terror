package sk.sivak.eldritchhorror.core.constants.location;

/**
 * @author msivak
 */
public class Path implements LocationInfo.Connection {

    private LocationId locationId;
    private PathType pathType;

    public Path(LocationId locationId, PathType pathType) {
        this.locationId = locationId;
        this.pathType = pathType;
    }

    @Override
    public LocationId getLocationId() {
        return locationId;
    }

    @Override
    public PathType getPathType() {
        return pathType;
    }
}
