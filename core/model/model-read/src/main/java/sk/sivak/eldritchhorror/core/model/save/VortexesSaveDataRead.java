package sk.sivak.eldritchhorror.core.model.save;

import sk.sivak.eldritchhorror.core.constants.clue.Clue;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;

import java.util.List;
import java.util.Map;

public interface VortexesSaveDataRead {

    List<LocationId> getSpawnedVortexes();
}
