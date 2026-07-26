package sk.sivak.eldritchhorror.core.model;

import sk.sivak.eldritchhorror.core.constants.mythos.MythosCard;
import sk.sivak.eldritchhorror.core.model.save.MythosDeckSaveDataRead;
import sk.sivak.eldritchhorror.core.model.save.SaveDataWrite;

public interface MythosDeckWrite extends MythosDeckRead{

    MythosCard drawMythosCard();

    SaveDataWrite.MythosDeckSaveDataWrite save();

    void load(MythosDeckSaveDataRead mythosDeckSaveData);
}
