package sk.sivak.eldritchhorror.core.model;

import sk.sivak.eldritchhorror.core.constants.ancientone.AncientOneInfo;
import sk.sivak.eldritchhorror.core.model.save.ModelSaveDataRead;
import sk.sivak.eldritchhorror.core.model.save.SaveDataWrite;

/**
 * @author msivak
 */
public interface ModelWrite extends ModelRead {

    void init();

    void setDiceRollSimulator(DiceRollSimulator diceRollSimulator);

    void initAvailableAncientOnes();

    void setActiveAncientOne(AncientOneInfo activeAnctionOne);

    void initReferenceCard(int players);

    void newRound();

    AncientOneWrite getAncientOne();

    BackgroundModelWrite getBackgroundModel();

    void setReckoningTriggered(boolean triggered);

    SaveDataWrite.ModelSaveDataWrite save();

    SaveDataWrite createSaveData();

    void load(ModelSaveDataRead modelSaveData);

    void setMythosCardTextEffect(boolean mythosCardTextEffect);
}
