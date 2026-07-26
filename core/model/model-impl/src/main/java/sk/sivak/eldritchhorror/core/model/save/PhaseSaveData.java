package sk.sivak.eldritchhorror.core.model.save;


import sk.sivak.eldritchhorror.core.constants.phase.PhaseType;

public class PhaseSaveData implements SaveDataWrite.PhaseSaveDataWrite {
    private PhaseType currentPhase;

    public void setCurrentPhase(PhaseType currentPhase) {
        this.currentPhase = currentPhase;
    }

    @Override
    public PhaseType getCurrentPhase() {
        return currentPhase;
    }
}
