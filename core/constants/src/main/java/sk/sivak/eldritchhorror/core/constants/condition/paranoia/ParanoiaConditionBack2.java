package sk.sivak.eldritchhorror.core.constants.condition.paranoia;

import sk.sivak.eldritchhorror.core.constants.condition.AbstractConditionBack;

public class ParanoiaConditionBack2 extends AbstractConditionBack {

    public ParanoiaConditionBack2() {
        title = "Kleptomania";
        flavorText = "Against rational thought, you compulsively take what does not belong to you and hide it away.";
        effectText = "[#GOOD]Gain one random Item from the deck.[]\n" +
                "Then, you must hide from the authorities (Test Observation).\n" +
                "If your test result is less than the value of the Item you gained,\n" +
                "[#BAD]discard that Item and gain a Detained Condition.[]\n" +
                "[#GOOD]Then discard this card.[]";
    }
}
