package sk.sivak.eldritchhorror.core.constants.condition.righteous;

import sk.sivak.eldritchhorror.core.constants.condition.AbstractConditionBack;

public class RighteousConditionBack2 extends AbstractConditionBack {

    public RighteousConditionBack2() {
        title = "Silver Twilight Alliance";
        flavorText = "The sound of chanting fills the dark room. " +
                "Getting the Order of the Silver Twilight Lodge to assist you in this ritual " +
                "has surely used up every last bit of your good karma. " +
                "When the ritual ends, you know the world is a safer place.";
        effectText = "[#GOOD]Close 1 Gate of your choice on any space[]\n" +
                "or [#GOOD]retreat Doom.[]\n" +
                "[#BAD]Then discard this card.[]";
    }
}
