package sk.sivak.eldritchhorror.core.model.save;

import sk.sivak.eldritchhorror.core.constants.gate.Gate;

import java.util.List;

public interface GateStackSaveDataRead {
    List<Gate> getGateList();

    List<Gate> getDiscardedGates();

    List<Gate> getSpawnedGates();
}
