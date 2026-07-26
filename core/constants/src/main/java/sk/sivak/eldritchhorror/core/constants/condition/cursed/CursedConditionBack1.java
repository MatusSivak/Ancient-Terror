package sk.sivak.eldritchhorror.core.constants.condition.cursed;

import sk.sivak.eldritchhorror.core.constants.condition.AbstractConditionBack;

public class CursedConditionBack1 extends AbstractConditionBack {

    public CursedConditionBack1() {
        title = "Curse of the Serpent";
        flavorText = "Snakes and lizards seem to gather wherever you are. " +
                "You sense something lurking in the wilds, waiting for you.";
        effectText = "If you are on a Wilderness space, there's no escape from the serpents.\n" +
                "[#BAD]You are Devoured.[]\n" +
                "If you are not on a Wilderness space, one of the serpents bites you.\n" +
                "[#BAD]Gain a Poisoned Condition[]\n" +
                "and [#GOOD]discard this card.[]";
    }
}
