package sk.sivak.eldritchhorror.core.constants.condition.righteous;

import sk.sivak.eldritchhorror.core.constants.condition.AbstractConditionBack;

public class RighteousConditionBack1 extends AbstractConditionBack {

    public RighteousConditionBack1() {
        title = "Bring The Fight To Them";
        flavorText = "You feel invigorated as you burst into the room. " +
                "Cult members scatter in all directions, " +
                "but the authorities round them up without a single cultist escaping. " +
                "The information they give you is highly illuminating.";

        effectText = "[#GOOD]Advance the active Mystery[]\n" +
                "or [#GOOD]gain 2 Clues.[]\n" +
                "[#BAD]Then discard this card.[]";
    }
}
