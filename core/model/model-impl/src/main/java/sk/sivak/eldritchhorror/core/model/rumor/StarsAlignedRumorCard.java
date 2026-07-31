package sk.sivak.eldritchhorror.core.model.rumor;

import sk.sivak.eldritchhorror.core.constants.location.LocationId;

public class StarsAlignedRumorCard extends AbstractRumorCardInfo{

    public StarsAlignedRumorCard() {
        setId("StarsAligned");
        setTitleText("rumor.card.starsAligned.title");
        setFlavorText("rumor.card.starsAligned.flavor");
        setObjectiveText("rumor.card.starsAligned.objective");
        setReckoningText("rumor.card.starsAligned.reckoning");
        setRumorLocation(LocationId.SPACE_7);
        setStormSpawned(true);
        // Clues = Investigators / 2
    }
}
