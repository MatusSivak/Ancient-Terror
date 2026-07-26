package sk.sivak.eldritchhorror.core.constants.condition.darkpact;

import sk.sivak.eldritchhorror.core.constants.condition.AbstractConditionBack;

public class DarkPactConditionBack1 extends AbstractConditionBack {

    public DarkPactConditionBack1() {
        title = "All is Lost";
        flavorText = "The agreement you made seemed necessary at the time, " +
                "but now you regret signing that pact. " +
                "The sinister beings have come to claim what is yours: blood, knowledge, and hope.";
        effectText = "[#BAD]Discard all of your possessions\n" +
                "and Improvements\n" +
                "and lose all but 1 Health\n" +
                "and all but 1 Sanity.[]\n" +
                "[#GOOD]Then discard this card.[]";
    }
}
