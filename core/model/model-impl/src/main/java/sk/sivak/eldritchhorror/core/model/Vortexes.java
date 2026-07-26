package sk.sivak.eldritchhorror.core.model;

import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.model.save.VortexesSaveData;
import sk.sivak.eldritchhorror.core.model.save.VortexesSaveDataRead;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class Vortexes implements VortexesWrite{
    private List<LocationId> spawnedVortexes;
    private List<Integer> rlyehRisenEncounters;
    private List<Integer> voidBetweenWorldsEncounters;
    private List<Integer> theKeyAndTheGateEncounters;

    @Override
    public void init() {
        spawnedVortexes = new LinkedList<>();
        initRlyehRisenEncounters();
        initVoidBetweenWorldsEncounters();
        initTheKeyAndTheGateEncounters();
    }

    @Override
    public Integer drawRlyehRisenEncounter() {
        if (rlyehRisenEncounters.isEmpty()) {
            initRlyehRisenEncounters();
        }
        return rlyehRisenEncounters.remove(0);
    }

    @Override
    public Integer drawVoidBetweenWorldsEncounter() {
        if (voidBetweenWorldsEncounters.isEmpty()) {
            initVoidBetweenWorldsEncounters();
        }
        return voidBetweenWorldsEncounters.remove(0);
    }

    @Override
    public Integer drawTheKeyAndTheGateEncounter() {
        if (theKeyAndTheGateEncounters.isEmpty()) {
            initTheKeyAndTheGateEncounters();
        }
        return theKeyAndTheGateEncounters.remove(0);
    }

    private void initRlyehRisenEncounters() {
        rlyehRisenEncounters = new LinkedList<>();
        for (int i = 1; i <= 8; i++) {
            rlyehRisenEncounters.add(i);
        }
        Collections.shuffle(rlyehRisenEncounters);
    }

    private void initVoidBetweenWorldsEncounters() {
        voidBetweenWorldsEncounters = new LinkedList<>();
        for (int i = 1; i <= 8; i++) {
            voidBetweenWorldsEncounters.add(i);
        }
        Collections.shuffle(voidBetweenWorldsEncounters);
    }

    private void initTheKeyAndTheGateEncounters() {
        theKeyAndTheGateEncounters = new LinkedList<>();
        for (int i = 1; i <= 8; i++) {
            theKeyAndTheGateEncounters.add(i);
        }
        Collections.shuffle(theKeyAndTheGateEncounters);
    }

    @Override
    public boolean isAtLocation(LocationId locationId) {
        return spawnedVortexes.contains(locationId);
    }

    @Override
    public void spawnVortex(LocationId locationId) {
        spawnedVortexes.add(locationId);
    }

    @Override
    public void removeVortex(LocationId locationId) {
        spawnedVortexes.remove(locationId);
    }

    @Override
    public List<LocationId> getSpawnedVortexes() {
        return new LinkedList<>(spawnedVortexes);
    }

    @Override
    public VortexesSaveData save() {
        VortexesSaveData vortexesSaveData = new VortexesSaveData();
        vortexesSaveData.setSpawnedVortexes(spawnedVortexes);
        return vortexesSaveData;
    }

    @Override
    public void load(VortexesSaveDataRead saveData) {
        init();
        if (saveData == null) {
            return;
        }
        if (saveData.getSpawnedVortexes() == null) {
            return;
        }
        spawnedVortexes = saveData.getSpawnedVortexes();
    }
}
