package sk.sivak.eldritchhorror.core.constants.condition.hallucinations;

import sk.sivak.eldritchhorror.core.constants.condition.AbstractConditionBack;

public class HallucinationsConditionBack3 extends AbstractConditionBack {

    public HallucinationsConditionBack3() {
        title = "The Voices";
        flavorText = "In the past, you've barely been able to hear the whispering at the edge of your consciousness. " +
                "Now that the voices are more audible, you can hear the horrible demands they are making of you.";
        effectText = "[#BAD]Lose 1 Sanity.\n" +
                "Then flip this card.[]";
    }
}
