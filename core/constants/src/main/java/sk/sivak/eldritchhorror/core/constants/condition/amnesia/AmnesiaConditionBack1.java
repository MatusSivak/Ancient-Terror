package sk.sivak.eldritchhorror.core.constants.condition.amnesia;

import sk.sivak.eldritchhorror.core.constants.condition.AbstractConditionBack;

public class AmnesiaConditionBack1 extends AbstractConditionBack {

    public AmnesiaConditionBack1() {
        title = "Unexplained Debts";
        flavorText = "According to the paperwork written in your own hand, you took out a loan and made unspecified promises of repayment. " +
                "To your dismay, there is no sign of the money or what you could have spent it on.";
        effectText = "[#BAD]Gain a Debt Condition.[]\n" +
                "[#GOOD]Then discard this card.[]";
    }
}
