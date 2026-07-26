package sk.sivak.eldritchhorror.core.constants.clue;

import sk.sivak.eldritchhorror.core.constants.location.LocationId;

/**
 * @author msivak
 */
public interface ClueInfo {

    LocationId getSpawnLocationId();

    LocationId getCurrentLocationId();
}
