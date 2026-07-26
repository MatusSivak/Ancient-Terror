package sk.sivak.eldritchhorror.core.model;

import sk.sivak.eldritchhorror.core.constants.location.LocationId;

import java.util.List;

/**
 * @author msivak
 */
public interface VortexesRead {
    boolean isAtLocation(LocationId locationId);

    List<LocationId> getSpawnedVortexes();
}
