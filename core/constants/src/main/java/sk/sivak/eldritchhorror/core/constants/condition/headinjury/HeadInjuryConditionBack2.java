package sk.sivak.eldritchhorror.core.constants.condition.headinjury;

import sk.sivak.eldritchhorror.core.constants.condition.AbstractConditionBack;

public class HeadInjuryConditionBack2 extends AbstractConditionBack {

    public HeadInjuryConditionBack2() {
        title = "Splitting Headache";
        flavorText = "Sharp pain splits your skull. " +
                "A screeching sound grows louder and louder until you cannot bear the agony any longer. " +
                "For hours you fantasize about puncturing your own ear drums to stop the excruciating ringing in your head.";
        effectText = "[#BAD]Lose 3 Sanity.[]\n" +
                "[#GOOD]Then discard this card.[]";
    }
}
