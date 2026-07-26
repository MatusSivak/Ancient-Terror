package sk.sivak.eldritchhorror.core.constants.location;

import java.util.List;

/**
 * @author msivak
 */
public interface LocationInfo {

    LocationId getLocationId();

    LocationType getLocationType();

    List<Connection> getConnections();

    List<Connection> getTrainConnections();

    List<Connection> getShipConnections();

    interface Connection {

        LocationId getLocationId();

        PathType getPathType();

    }

}
