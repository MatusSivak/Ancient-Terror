package sk.sivak.eldritchhorror.core.model.save;

import sk.sivak.eldritchhorror.core.constants.clue.Clue;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class CluePoolSaveData implements SaveDataWrite.CluePoolSaveDataWrite {
    private Map<String, List<Clue>> investigatorCluesMap = new HashMap<>();
    private List<Clue> spawnedClues = new LinkedList<>();

    public void setInvestigatorCluesMap(Map<String, List<Clue>> investigatorCluesMap) {
        this.investigatorCluesMap = investigatorCluesMap;
    }

    public void setSpawnedClues(List<Clue> spawnedClues) {
        this.spawnedClues = spawnedClues;
    }

    @Override
    public Map<String, List<Clue>> getInvestigatorCluesMap() {
        return investigatorCluesMap;
    }

    @Override
    public List<Clue> getSpawnedClues() {
        return spawnedClues;
    }
}