package sk.sivak.eldritchhorror.core.model.rumor;

import sk.sivak.eldritchhorror.core.constants.location.LocationId;

public class FracturedRealityRumorCard extends AbstractRumorCardInfo{

    public FracturedRealityRumorCard() {
        setId("FracturedReality");
        setTitleText("Fractured Reality");
        setFlavorText("The strange phenomenon is an echo\nof the catastrophic destruction of Mu.\n" +
                "The repercussions of the serpent people's\noverreaching ambition still take their toll.");
        setObjectiveText("[#GOOD]On a Rumor location, close an ancient portal created by a long-dead wizard of Mu.[]");
        setFailureText("[#BAD]Doom advances for each spawned Gate.[]");
        setTimeRemaining(4);
        setRumorLocation(LocationId.SPACE_2);
        setStormSpawned(true);
        setReckoningText("[#BAD]Countdown.[]");

    }

}
