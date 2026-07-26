package sk.sivak.eldritchhorror.core.model.rumor;

import sk.sivak.eldritchhorror.core.constants.location.LocationId;

public class StarsAlignedRumorCard extends AbstractRumorCardInfo{

    public StarsAlignedRumorCard() {
        setId("StarsAligned");
        setTitleText("Stars Aligned");
        setFlavorText("A dozen unpleasant-looking strangers have settled into Panama\n" +
                "with a large collection of astronomical reference books.\n" +
                "There is something unique about this place and time\n" +
                "that allows them to rip apart the fabric of reality");
        setObjectiveText("[#GOOD]On a Rumor location, find and remove these strangers.[]");
        setReckoningText("[#BAD]Omen advances.[]");
        setRumorLocation(LocationId.SPACE_7);
        setStormSpawned(true);
        // Clues = Investigators / 2
    }
}
