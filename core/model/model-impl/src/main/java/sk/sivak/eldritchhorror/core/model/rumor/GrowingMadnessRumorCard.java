package sk.sivak.eldritchhorror.core.model.rumor;

import sk.sivak.eldritchhorror.core.constants.location.LocationId;

public class GrowingMadnessRumorCard extends AbstractRumorCardInfo{

    public GrowingMadnessRumorCard() {
        setId("GrowingMadness");
        setTitleText("rumor.card.growingMadness.title");
        setFlavorText("rumor.card.growingMadness.flavor");
        setObjectiveText("rumor.card.growingMadness.objective");
        setFailureText("rumor.card.growingMadness.failure");
        setReckoningText("rumor.card.growingMadness.reckoning");
        setRumorLocation(LocationId.SPACE_8);
        setStormSpawned(true);
        setTimeRemaining(4);
        // setCluesRequired();
    }
}
