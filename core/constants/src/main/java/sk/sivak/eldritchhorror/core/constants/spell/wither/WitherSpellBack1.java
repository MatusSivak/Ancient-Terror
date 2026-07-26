package sk.sivak.eldritchhorror.core.constants.spell.wither;

import sk.sivak.eldritchhorror.core.constants.spell.AbstractSpellBack;
import sk.sivak.eldritchhorror.core.constants.spell.SpellEffectImpl;

public class WitherSpellBack1 extends AbstractSpellBack {

    public WitherSpellBack1() {
        // Discard this card unless you lose 2 Sanity.
        addSpellEffect("0-1", new SpellEffectImpl("The spell's dark energy turns against you, flooding your mind with madness."));

        //  Lose 1 Sanity and gain +5 Strength during that encounter instead.
        addSpellEffect("2+", new SpellEffectImpl("The incantation takes effect with horrifying success."));
    }
}
