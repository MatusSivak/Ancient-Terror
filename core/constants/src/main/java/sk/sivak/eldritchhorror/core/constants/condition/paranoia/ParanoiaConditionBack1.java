package sk.sivak.eldritchhorror.core.constants.condition.paranoia;

import sk.sivak.eldritchhorror.core.constants.condition.AbstractConditionBack;

public class ParanoiaConditionBack1 extends AbstractConditionBack {

    public ParanoiaConditionBack1() {
        title = "Violent Outbursts";
        flavorText = "You've felt the anger coiled up inside of you for such a long time. " +
                "You just can't control it any longer. You lash out at anyone near you.";
        effectText = "[#BAD]Each other investigator\n" +
                "on your space loses 2 Health\n" +
                "and you discard one Ally.\n" +
                "Then flip this card.[]";
    }
}
