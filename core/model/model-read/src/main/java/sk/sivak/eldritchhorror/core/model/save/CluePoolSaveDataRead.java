package sk.sivak.eldritchhorror.core.model.save;

import sk.sivak.eldritchhorror.core.constants.clue.Clue;

import java.util.List;
import java.util.Map;

public interface CluePoolSaveDataRead {
    Map<String, List<Clue>> getInvestigatorCluesMap();

    List<Clue> getSpawnedClues();
}
