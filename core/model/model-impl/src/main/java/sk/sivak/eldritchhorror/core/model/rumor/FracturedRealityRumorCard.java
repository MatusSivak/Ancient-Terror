package sk.sivak.eldritchhorror.core.model.rumor;

import sk.sivak.eldritchhorror.core.constants.location.LocationId;

public class FracturedRealityRumorCard extends AbstractRumorCardInfo{

    public FracturedRealityRumorCard() {
        setId("FracturedReality");
        setTitleText("rumor.card.fracturedReality.title");
        setFlavorText("rumor.card.fracturedReality.flavor");
        setObjectiveText("rumor.card.fracturedReality.objective");
        setFailureText("rumor.card.fracturedReality.failure");
        setTimeRemaining(4);
        setRumorLocation(LocationId.SPACE_2);
        setStormSpawned(true);
        setReckoningText("rumor.card.fracturedReality.reckoning");

    }

}
