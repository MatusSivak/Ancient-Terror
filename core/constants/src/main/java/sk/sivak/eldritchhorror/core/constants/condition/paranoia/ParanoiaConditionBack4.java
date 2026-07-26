package sk.sivak.eldritchhorror.core.constants.condition.paranoia;

import sk.sivak.eldritchhorror.core.constants.condition.AbstractConditionBack;

public class ParanoiaConditionBack4 extends AbstractConditionBack {

    public ParanoiaConditionBack4() {
        title = "Self-Doubt";
        flavorText = "Your friends and colleagues were right. " +
                "What hope do you have of changing anything? " +
                "Mere mortals couldn't possibly stop ancient, powerful beings.";
        effectText = "[#BAD]Discard all of your Improvements.[]\n" +
                "[#GOOD]Then discard this card.[]";
    }
}
