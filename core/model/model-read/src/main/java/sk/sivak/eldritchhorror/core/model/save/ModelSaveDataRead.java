package sk.sivak.eldritchhorror.core.model.save;

public interface ModelSaveDataRead {
    int getInvestigatorCount();

    int getCurrentRound();

    Difficulty getDifficulty();

    enum Difficulty {
        EASY,
        NORMAL
    }
}
