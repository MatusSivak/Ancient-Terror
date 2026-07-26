package sk.sivak.eldritchhorror.core.model.save;

import sk.sivak.eldritchhorror.core.constants.clue.Clue;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class VortexesSaveData implements SaveDataWrite.VortexesSaveDataWrite {
    private List<LocationId> spawnedVortexes = new LinkedList<>();

    public void setSpawnedVortexes(List<LocationId> spawnedVortexes) {
        this.spawnedVortexes = spawnedVortexes;
    }

    @Override
    public List<LocationId> getSpawnedVortexes() {
        return spawnedVortexes;
    }
}