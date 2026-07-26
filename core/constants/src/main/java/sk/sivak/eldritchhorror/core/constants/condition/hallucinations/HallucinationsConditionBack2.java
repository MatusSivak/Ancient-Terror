package sk.sivak.eldritchhorror.core.constants.condition.hallucinations;

import sk.sivak.eldritchhorror.core.constants.condition.AbstractConditionBack;

public class HallucinationsConditionBack2 extends AbstractConditionBack {

    public HallucinationsConditionBack2() {
        title = "Swarming";
        flavorText = "The bugs are everywhere. " +
                "They crawl across your skin and into your mouth.";
        effectText = "[#BAD]Lose 3 Sanity.[]\n" +
                "[#GOOD]Then discard this card.[]";
    }
}
