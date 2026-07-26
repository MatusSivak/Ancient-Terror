package sk.sivak.eldritchhorror.core.model;

import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.model.save.ExpeditionDeckSaveDataRead;
import sk.sivak.eldritchhorror.core.model.save.SaveDataWrite;

/**
 * @author msivak
 */
public interface ExpeditionDeckWrite extends ExpeditionDeckRead {

    void initExpeditionDeck();

    void discardTopCard();

    void destroyCurrentExpeditionLocation();

    SaveDataWrite.ExpeditionDeckSaveDataWrite save();

    void load(ExpeditionDeckSaveDataRead expeditionDeckSaveData);
}
