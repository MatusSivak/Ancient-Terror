package sk.sivak.eldritchhorror.core.model;

import sk.sivak.eldritchhorror.core.constants.clue.Clue;
import sk.sivak.eldritchhorror.core.constants.clue.ClueInfo;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;

import java.util.List;
import java.util.Set;

/**
 * @author msivak
 */
public interface CluePoolRead {

    int getClueCount(InvestigatorId investigatorId);

    boolean isClueAt(LocationId locationId);

    Set<LocationId> getClueLocations();

    List<? extends ClueInfo> getSpawnedClues();

    LocationId getClueCurrentLocationId(LocationId spawnLocationId);

    boolean isCluePoolEmpty();
}
