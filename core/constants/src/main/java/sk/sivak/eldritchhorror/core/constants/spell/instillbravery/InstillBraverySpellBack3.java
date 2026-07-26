package sk.sivak.eldritchhorror.core.constants.spell.instillbravery;

import sk.sivak.eldritchhorror.core.constants.spell.AbstractSpellBack;
import sk.sivak.eldritchhorror.core.constants.spell.SpellEffectImpl;

public class InstillBraverySpellBack3 extends AbstractSpellBack {

    public InstillBraverySpellBack3() {
        // That investigator gains 1 Madness Condition.
        addSpellEffect("0", new SpellEffectImpl("You feel your consciousness being altered in some way."));

        // Lose 1 Health.
        addSpellEffect("1", new SpellEffectImpl("Casting the magic requires a payment of blood."));

        // No additional effect.
        addSpellEffect("2+", new SpellEffectImpl("The chant echoes in your mind and heart, inspiring your courage."));
    }
}
