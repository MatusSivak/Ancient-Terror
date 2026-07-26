package sk.sivak.eldritchhorror.core.model.save;


import sk.sivak.eldritchhorror.core.constants.MysteryCardId;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;

import java.util.LinkedList;
import java.util.List;

public class MysteryDeckSaveData implements SaveDataWrite.MysteryDeckSaveDataWrite {
    private List<MysteryCardId> mysteryCards = new LinkedList<>();
    private int solvedMysteries;
    private int progress;
    private List<LocationId> pinLocations = new LinkedList<>();

    public void setMysteryCards(List<MysteryCardId> mysteryCards) {
        this.mysteryCards = mysteryCards;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public void setPinLocations(List<LocationId> pinLocations) {
        this.pinLocations = pinLocations;
    }

    public void setSolvedMysteries(int solvedMysteries) {
        this.solvedMysteries = solvedMysteries;
    }

    @Override
    public int getSolvedMysteries() {
        return solvedMysteries;
    }

    @Override
    public List<MysteryCardId> getMysteryCards() {
        return mysteryCards;
    }

    @Override
    public int getProgress() {
        return progress;
    }

    @Override
    public List<LocationId> getPinLocations() {
        return pinLocations;
    }
}