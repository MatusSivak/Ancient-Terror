package sk.sivak.eldritchhorror.core.model;

import sk.sivak.eldritchhorror.core.constants.clue.ClueInfo;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.model.save.CluePoolSaveDataRead;
import sk.sivak.eldritchhorror.core.model.save.SaveDataWrite;

/**
 * @author msivak
 */
public interface CluePoolWrite extends CluePoolRead {

    void initCluePool();

    void gainClueFromPool(InvestigatorId investigatorId);

    LocationId gainClueFromSpace(InvestigatorId investigatorId, LocationId locationId);

    void loseClue(InvestigatorId investigatorId);

    ClueInfo spawnClue();

    ClueInfo spawnClue(LocationId locationId);

    LocationId discardClue(LocationId locationId);

    void updateClueLocation(LocationId spawnLocationId, LocationId currentLocationId);

    void addOnTop(LocationId locationId);

    void tradeClues(InvestigatorId investigatorA, InvestigatorId investigatorB, int difference);

    SaveDataWrite.CluePoolSaveDataWrite save();

    void load(CluePoolSaveDataRead cluePoolSaveData);

}
