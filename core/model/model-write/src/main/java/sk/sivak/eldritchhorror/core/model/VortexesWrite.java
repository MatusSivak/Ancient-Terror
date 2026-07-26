package sk.sivak.eldritchhorror.core.model;

import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.model.save.SaveDataWrite;
import sk.sivak.eldritchhorror.core.model.save.VortexesSaveDataRead;

import java.util.List;

/**
 * @author msivak
 */
public interface VortexesWrite extends VortexesRead {

    void init();

    Integer drawRlyehRisenEncounter();

    Integer drawVoidBetweenWorldsEncounter();

    Integer drawTheKeyAndTheGateEncounter();

    void spawnVortex(LocationId locationId);

    void removeVortex(LocationId locationId);

    SaveDataWrite.VortexesSaveDataWrite save();

    void load(VortexesSaveDataRead saveData);
}
