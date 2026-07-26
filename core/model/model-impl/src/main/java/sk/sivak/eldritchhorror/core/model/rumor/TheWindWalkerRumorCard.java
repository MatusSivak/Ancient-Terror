package sk.sivak.eldritchhorror.core.model.rumor;

import sk.sivak.eldritchhorror.core.constants.location.LocationId;

public class TheWindWalkerRumorCard extends AbstractRumorCardInfo{

    public TheWindWalkerRumorCard() {
        setId("TheWindWalker");
        setTitleText("The Wind-Walker");
        setFlavorText("The weather grows worse. Snow and ice cover cities\nthat have never seen such weather before in their history.");
        setObjectiveText("[#GOOD]Defeat The Wind-Walker.[]");
        setFailureText("[#BAD]-Each investigator becomes Delayed.\n-Each investigator loses 6 Health.[]");
        setReckoningText("[#BAD]Countdown.[]");
        setTimeRemaining(4);
        setStormSpawned(false);
        setRumorLocation(LocationId.SPACE_4);

    }
}
