package sk.sivak.eldritchhorror.core.model;

import sk.sivak.eldritchhorror.core.constants.location.LocationType;
import sk.sivak.eldritchhorror.core.model.save.SaveDataWrite;

public interface GeneralEncounterDeckWrite {

    void init();

    Integer drawCard(LocationType locationType);

    SaveDataWrite.GeneralEncounterDeckSaveDataWrite save();
}
