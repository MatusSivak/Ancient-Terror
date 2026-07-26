package sk.sivak.eldritchhorror.core.constants.condition.paranoia;

import sk.sivak.eldritchhorror.core.constants.condition.AbstractConditionBack;

public class ParanoiaConditionBack3 extends AbstractConditionBack {

    public ParanoiaConditionBack3() {
        title = "Agoraphobia";
        flavorText = "You cannot tolerate the company of other people any longer. " +
                "The seething masses of humanity are a blight on the face of the planet.";
        effectText = "If you are on a City space, the presence of other people erodes your reason.\n" +
                "[#BAD]Lose 2 Sanity.\n" +
                "Then flip this card.[]";
    }
}
