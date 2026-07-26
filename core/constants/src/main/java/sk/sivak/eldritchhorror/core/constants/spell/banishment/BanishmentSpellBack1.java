package sk.sivak.eldritchhorror.core.constants.spell.banishment;

import sk.sivak.eldritchhorror.core.constants.spell.AbstractSpellBack;
import sk.sivak.eldritchhorror.core.constants.spell.SpellEffectImpl;

public class BanishmentSpellBack1 extends AbstractSpellBack {

    public BanishmentSpellBack1() {
        // If you rolled any 1's, discard this card.
        addSpellEffect("0", new SpellEffectImpl("You stutter the words and are suddenly unable to recall them."));
        // Lose 1 Sanity.
        addSpellEffect("1+", new SpellEffectImpl("The magic warps your perception of time and space."));
    }
}
