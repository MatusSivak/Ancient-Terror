package sk.sivak.eldritchhorror.core.constants.condition.cursed;

import sk.sivak.eldritchhorror.core.constants.condition.AbstractConditionBack;

public class CursedConditionBack2 extends AbstractConditionBack {

    public CursedConditionBack2() {
        title = "A Dark Betrayal";
        flavorText = "A familiar figure from another world has stepped through the rift. " +
                "A creature you've met only in your worst nightmares.";
        effectText = "[#BAD]Spawn the Doppelganger Epic Monster[]\n" +
                "on the nearest Gate. If the Doppelganger Epic Monster spawned on your space,\n" +
                "[#BAD]you are Devoured.[]";
    }
}
