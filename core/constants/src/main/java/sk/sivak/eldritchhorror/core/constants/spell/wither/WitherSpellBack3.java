package sk.sivak.eldritchhorror.core.constants.spell.wither;

import sk.sivak.eldritchhorror.core.constants.spell.AbstractSpellBack;
import sk.sivak.eldritchhorror.core.constants.spell.SpellEffectImpl;

public class WitherSpellBack3 extends AbstractSpellBack {

    public WitherSpellBack3() {
        // If you rolled any 1's, discard this card.
        addSpellEffect("0", new SpellEffectImpl("You misspeak the incantation, causing the magic to dissipate."));

        // Lose 1 Health.
        addSpellEffect("1", new SpellEffectImpl("The harmful magic lingers in your body."));

        // No additional effect.
        addSpellEffect("2+", new SpellEffectImpl("The magic takes hold in your target's body. "));
    }
}
