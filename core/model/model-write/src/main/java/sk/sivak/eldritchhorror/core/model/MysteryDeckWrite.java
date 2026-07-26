package sk.sivak.eldritchhorror.core.model;

import sk.sivak.eldritchhorror.core.constants.MysteryCardInfo;
import sk.sivak.eldritchhorror.core.constants.ancientone.AncientOneId;
import sk.sivak.eldritchhorror.core.model.save.MysteryDeckSaveDataRead;
import sk.sivak.eldritchhorror.core.model.save.SaveDataWrite;

import java.util.List;

/**
 * @author msivak
 */
public interface MysteryDeckWrite extends MysteryDeckRead {

    void initMysteryDeck(AncientOneId ancientOneId, Integer nrOfInvestigators);

    MysteryCardInfo drawNewMystery();

    void solveCurrentMystery();

    SaveDataWrite.MysteryDeckSaveDataWrite save();

    void load(MysteryDeckSaveDataRead mysteryDeckSaveData);
}
