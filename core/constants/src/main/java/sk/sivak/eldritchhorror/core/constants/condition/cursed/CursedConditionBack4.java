package sk.sivak.eldritchhorror.core.constants.condition.cursed;

import sk.sivak.eldritchhorror.core.constants.condition.AbstractConditionBack;

public class CursedConditionBack4 extends AbstractConditionBack {

    public CursedConditionBack4() {
        title = "Cosmic Instability";
        flavorText = "When planets and stars align, " +
                "the eclipsed star becomes a portal to another world. " +
                "Looking at such an eclipse can transport one's mind through the depths of the cosmos.";
        effectText = "If the Omen is on the red space of the track,\n" +
                "[#BAD]you are Devoured.[]\n" +
                "If the Omen is not on the red space of the track,\n" +
                "[#BAD]spawn 2 Gates.[]\n" +
                "[#GOOD]Then discard this card.[]";
    }
}
