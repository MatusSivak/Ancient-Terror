package sk.sivak.eldritchhorror.core.model.save;

import sk.sivak.eldritchhorror.core.constants.MysteryCardId;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;

import java.util.List;

public interface MysteryDeckSaveDataRead {
    List<MysteryCardId> getMysteryCards();

    int getProgress();

    int getSolvedMysteries();

    List<LocationId> getPinLocations();
}
