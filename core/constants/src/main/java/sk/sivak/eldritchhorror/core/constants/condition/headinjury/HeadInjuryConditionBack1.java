package sk.sivak.eldritchhorror.core.constants.condition.headinjury;

import sk.sivak.eldritchhorror.core.constants.condition.AbstractConditionBack;

public class HeadInjuryConditionBack1 extends AbstractConditionBack {

    public HeadInjuryConditionBack1() {
        title = "Brain Aneurysm";
        flavorText = "The fog has returned, clouding your mind and memories. " +
                "You massage your temples, waiting for the headache to pass. " +
                "You suddenly realize blood is dripping from your ears and eyes. " +
                "Your vision goes black as you collapse.";
        effectText = "[#BAD]Lose 3 Health.[]\n" +
                "[#GOOD]Then discard this card.[]";
    }
}
