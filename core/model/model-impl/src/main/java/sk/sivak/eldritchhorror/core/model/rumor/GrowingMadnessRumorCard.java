package sk.sivak.eldritchhorror.core.model.rumor;

import sk.sivak.eldritchhorror.core.constants.location.LocationId;

public class GrowingMadnessRumorCard extends AbstractRumorCardInfo{

    public GrowingMadnessRumorCard() {
        setId("GrowingMadness");
        setTitleText("Growing Madness");
        setFlavorText("You dream of a life as an insane wizard\nthousands of years ago in Atlantis.\n" +
                "You also dream of that same wizard\nstill alive today on an uncharted island.");
        setObjectiveText("[#GOOD]On a Rumor location, find the uncharted isle.[]");
        setFailureText("[#BAD]Each investigator loses three Sanity.[]");
        setReckoningText("[#BAD]Lead investigator gains a Madness Condition. Countdown for each Madness Condition he has.[]");
        setRumorLocation(LocationId.SPACE_8);
        setStormSpawned(true);
        setTimeRemaining(4);
        // setCluesRequired();
    }
}
