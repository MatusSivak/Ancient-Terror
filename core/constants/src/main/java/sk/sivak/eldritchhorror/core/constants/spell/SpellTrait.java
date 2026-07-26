package sk.sivak.eldritchhorror.core.constants.spell;

import sk.sivak.eldritchhorror.core.constants.card.Trait;

public enum SpellTrait implements Trait {

    INCANTATION, GLAMOUR, RITUAL, TEAMWORK;

    @Override
    public String asString() {
        return name().substring(0, 1).toUpperCase() + name().substring(1).toLowerCase();
    }
}
