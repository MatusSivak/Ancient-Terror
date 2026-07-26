package sk.sivak.eldritchhorror.core.model.save;

public class ModelSaveData implements SaveDataWrite.ModelSaveDataWrite {
    private int investigatorCount;
    private int currentRound;
    private Difficulty difficulty;

    public void setInvestigatorCount(int investigatorCount) {
        this.investigatorCount = investigatorCount;
    }

    public void setCurrentRound(int currentRound) {
        this.currentRound = currentRound;
    }

    @Override
    public int getInvestigatorCount() {
        return investigatorCount;
    }

    @Override
    public int getCurrentRound() {
        return currentRound;
    }

    @Override
    public Difficulty getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(Difficulty difficulty) {
        this.difficulty = difficulty;
    }
}
