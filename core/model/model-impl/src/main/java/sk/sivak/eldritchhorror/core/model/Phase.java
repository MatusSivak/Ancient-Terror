package sk.sivak.eldritchhorror.core.model;

import sk.sivak.eldritchhorror.core.constants.phase.PhaseType;
import sk.sivak.eldritchhorror.core.constants.tracker.AnalyticsCategory;
import sk.sivak.eldritchhorror.core.constants.tracker.GoogleServicesHolder;
import sk.sivak.eldritchhorror.core.model.save.PhaseSaveData;
import sk.sivak.eldritchhorror.core.model.save.PhaseSaveDataRead;

import java.util.Locale;

public class Phase implements PhaseWrite {

    private Long phaseStartTime;
    private PhaseType currentPhase;


    @Override
    public void init() {
        phaseStartTime = null;
        currentPhase = null;
    }

    @Override
    public PhaseType getPhaseType() {
        return currentPhase;
    }

    @Override
    public void changePhase() {
        if (currentPhase == null) {
            phaseStartTime = System.currentTimeMillis();
            currentPhase = PhaseType.ACTION;
            return;
        }
        trackPhaseDuration(currentPhase);
        switch (currentPhase) {
            case ACTION:
                currentPhase = PhaseType.ENCOUNTER;
                break;
            case ENCOUNTER:
                currentPhase = PhaseType.MYTHOS;
                break;
            case MYTHOS:
                currentPhase = PhaseType.ACTION;
                break;
        }
    }

    private void trackPhaseDuration(PhaseType currentPhase) {
        long playTime = (System.currentTimeMillis() - phaseStartTime);
        GoogleServicesHolder.getAnalyticsTracker().trackTiming(
                AnalyticsCategory.PHASE_DURATION, currentPhase.toString(), playTime);
        phaseStartTime = System.currentTimeMillis();
    }

    @Override
    public PhaseSaveData save() {
        PhaseSaveData phaseSaveData = new PhaseSaveData();
        phaseSaveData.setCurrentPhase(currentPhase);
        return phaseSaveData;
    }

    @Override
    public void load(PhaseSaveDataRead saveData) {
        phaseStartTime = System.currentTimeMillis();
        currentPhase = saveData.getCurrentPhase();
    }
}
