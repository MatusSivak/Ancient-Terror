package sk.sivak.eldritchhorror.core.constants.condition;

import sk.sivak.eldritchhorror.core.constants.card.Trait;

public enum ConditionTrait implements Trait {

    DEAL, COMMON, BOON, ILLNESS, INJURY, BANE, RESTRICTION, MADNESS;

    @Override
    public String asString() {
        return name().substring(0, 1).toUpperCase() + name().substring(1).toLowerCase();
    }
}
