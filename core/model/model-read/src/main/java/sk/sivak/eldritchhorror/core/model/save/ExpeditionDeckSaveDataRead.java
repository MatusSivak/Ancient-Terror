package sk.sivak.eldritchhorror.core.model.save;

import sk.sivak.eldritchhorror.core.constants.location.LocationId;

import java.util.List;

public interface ExpeditionDeckSaveDataRead {
    List<LocationId> getExpeditionDeck();
}
