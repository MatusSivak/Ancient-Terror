package sk.sivak.eldritchhorror.core.model;

import sk.sivak.eldritchhorror.core.constants.gate.GateColor;
import sk.sivak.eldritchhorror.core.constants.gate.GateInfo;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.model.save.GateStackSaveDataRead;
import sk.sivak.eldritchhorror.core.model.save.SaveDataWrite;

/**
 * @author msivak
 */
public interface GateStackWrite extends GateStackRead {

    Integer drawGateEncounter();

    void initGateStack();

    GateInfo spawnGate();

    GateColor closeGate(LocationId location);

    void putGateToTop(GateInfo selectedGate);

    void putGateToBottom(GateInfo selectedGate);

    SaveDataWrite.GateStackSaveDataWrite save();

    void load(GateStackSaveDataRead gateStackSaveData);
}
