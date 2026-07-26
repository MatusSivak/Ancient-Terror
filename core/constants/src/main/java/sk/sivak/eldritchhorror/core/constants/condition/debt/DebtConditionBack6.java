package sk.sivak.eldritchhorror.core.constants.condition.debt;

import sk.sivak.eldritchhorror.core.constants.condition.AbstractConditionBack;

public class DebtConditionBack6 extends AbstractConditionBack {

    public DebtConditionBack6() {
        title = "Debt Collector";
        flavorText = "The man in the silk suit hands you a large stack of papers. " +
                "\"I believe you'll find this offer quite reasonable.\" " +
                "His smile reminds you of a shark. " +
                "It's clear that he will not take \"no\" for an answer.";
        effectText = "Test Influence. If you fail,\n" +
                "[#BAD]discard 2 Item possessions.[]\n" +
                "[#GOOD]Then discard this card.[]";
    }
}
