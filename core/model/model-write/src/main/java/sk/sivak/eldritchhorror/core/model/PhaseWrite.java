package sk.sivak.eldritchhorror.core.model;

import sk.sivak.eldritchhorror.core.model.save.PhaseSaveDataRead;
import sk.sivak.eldritchhorror.core.model.save.SaveDataWrite;

public interface PhaseWrite extends PhaseRead {
    void init();

    void changePhase();

    SaveDataWrite.PhaseSaveDataWrite save();

    void load(PhaseSaveDataRead saveData);
}
