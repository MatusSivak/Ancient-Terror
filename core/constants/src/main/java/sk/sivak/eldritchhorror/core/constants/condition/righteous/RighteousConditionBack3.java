package sk.sivak.eldritchhorror.core.constants.condition.righteous;

import sk.sivak.eldritchhorror.core.constants.condition.AbstractConditionBack;

public class RighteousConditionBack3 extends AbstractConditionBack {

    public RighteousConditionBack3() {
        title = "That Which Does Not Kill Us";
        flavorText = "The horrid creature wraps its foul tendril around your leg, " +
                "pulling you into the dark. " +
                "\"I'm not dying here!\" you shout, " +
                "firing into the beast's flesh until it releases you. " +
                "After escaping, you feel as though you could take on any threat.";
        effectText = "[#GOOD]Discard any number of your Conditions[]\n" +
                "or [#GOOD]improve 2 skills of your choice.[]\n" +
                "[#BAD]Then discard this card.[]";
    }
}
