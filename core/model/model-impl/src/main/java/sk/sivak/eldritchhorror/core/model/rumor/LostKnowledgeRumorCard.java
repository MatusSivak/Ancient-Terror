package sk.sivak.eldritchhorror.core.model.rumor;

import sk.sivak.eldritchhorror.core.constants.location.LocationId;

public class LostKnowledgeRumorCard extends AbstractRumorCardInfo {

    public LostKnowledgeRumorCard() {
        setId("LostKnowledge");
        setTitleText("Lost Knowledge");
        setFlavorText("Your contact in the capital is hesitant\nto speak about those called \"The Watches\".\n" +
                "He says they work for a number of different governments,\nbut answer to some other authority.");
        setObjectiveText("[#GOOD]Defeat the Tick Tock Men.[]");
        setFailureText("[#BAD]-All Clues on the game board are discarded.[]\n" +
                "[#BAD]-Each investigator discards all Clues.[]");
        setReckoningText("[#BAD]Countdown.[]");
        setTimeRemaining(3);
        setStormSpawned(false);
        setRumorLocation(LocationId.SPACE_21);
    }
}
