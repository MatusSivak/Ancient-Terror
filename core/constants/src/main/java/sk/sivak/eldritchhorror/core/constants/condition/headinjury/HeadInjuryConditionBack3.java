package sk.sivak.eldritchhorror.core.constants.condition.headinjury;

import sk.sivak.eldritchhorror.core.constants.condition.AbstractConditionBack;

public class HeadInjuryConditionBack3 extends AbstractConditionBack {

    public HeadInjuryConditionBack3() {
        title = "Brain Damage";
        flavorText = "Ever since that blow to your head, " +
                "you have not been able to grasp the words necessary to communicate with others " +
                "nor have you retained your manual dexterity nor your memories.";
        effectText = "[#BAD]Lose 1 Sanity\n" +
                "and discard all of your Improvements.[]\n" +
                "[#GOOD]Then discard this card.[]";
    }
}
