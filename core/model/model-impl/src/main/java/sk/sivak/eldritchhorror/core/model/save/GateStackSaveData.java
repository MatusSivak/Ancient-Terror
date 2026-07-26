package sk.sivak.eldritchhorror.core.model.save;

import sk.sivak.eldritchhorror.core.constants.gate.Gate;

import java.util.LinkedList;
import java.util.List;

public class GateStackSaveData implements SaveDataWrite.GateStackSaveDataWrite {
    private List<Gate> gateList = new LinkedList<>();
    private List<Gate> discardedGates = new LinkedList<>();
    private List<Gate> spawnedGates = new LinkedList<>();

    public void setGateList(List<Gate> gateList) {
        this.gateList = gateList;
    }

    public void setDiscardedGates(List<Gate> discardedGates) {
        this.discardedGates = discardedGates;
    }

    public void setSpawnedGates(List<Gate> spawnedGates) {
        this.spawnedGates = spawnedGates;
    }

    @Override
    public List<Gate> getGateList() {
        return gateList;
    }

    @Override
    public List<Gate> getDiscardedGates() {
        return discardedGates;
    }

    @Override
    public List<Gate> getSpawnedGates() {
        return spawnedGates;
    }
}
