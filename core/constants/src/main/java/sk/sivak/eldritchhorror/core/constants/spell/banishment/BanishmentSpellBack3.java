package sk.sivak.eldritchhorror.core.constants.spell.banishment;

import sk.sivak.eldritchhorror.core.constants.spell.AbstractSpellBack;
import sk.sivak.eldritchhorror.core.constants.spell.SpellEffectImpl;

public class BanishmentSpellBack3 extends AbstractSpellBack {

    public BanishmentSpellBack3() {
        // Resolve a Monster surge on the nearest space containing a Gate.
        addSpellEffect("0", new SpellEffectImpl("Something in the words has become reversed."));
        // Lose 1 Sanity.
        addSpellEffect("1-2", new SpellEffectImpl("A vision of creatures from alien worlds wracks your mind."));
        // Discard any number of Monsters on that space with total toughness equal to or less than your test result instead.
        addSpellEffect("3+", new SpellEffectImpl("The magic works to great effect."));
    }
}
