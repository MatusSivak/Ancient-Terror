package sk.sivak.eldritchhorror.core.model.save;

import sk.sivak.eldritchhorror.core.constants.location.LocationId;

import java.util.List;

public class ExpeditionDeckSaveData implements SaveDataWrite.ExpeditionDeckSaveDataWrite {
    private List<LocationId> expeditionDeck;

    public void setExpeditionDeck(List<LocationId> expeditionDeck) {
        this.expeditionDeck = expeditionDeck;
    }

    @Override
    public List<LocationId> getExpeditionDeck() {
        return expeditionDeck;
    }
}
