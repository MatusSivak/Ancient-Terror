package sk.sivak.eldritchhorror.core.model.rumor;

import sk.sivak.eldritchhorror.core.constants.location.LocationId;

public class TheWindWalkerRumorCard extends AbstractRumorCardInfo{

    public TheWindWalkerRumorCard() {
        setId("TheWindWalker");
        setTitleText("rumor.card.windWalker.title");
        setFlavorText("rumor.card.windWalker.flavor");
        setObjectiveText("rumor.card.windWalker.objective");
        setFailureText("rumor.card.windWalker.failure");
        setReckoningText("rumor.card.windWalker.reckoning");
        setTimeRemaining(4);
        setStormSpawned(false);
        setRumorLocation(LocationId.SPACE_4);

    }
}
