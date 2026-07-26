package sk.sivak.eldritchhorror.core.constants.spell.feedthemind;

import sk.sivak.eldritchhorror.core.constants.spell.AbstractSpellBack;
import sk.sivak.eldritchhorror.core.constants.spell.SpellEffectImpl;

public class FeedTheMindSpellBack1 extends AbstractSpellBack {

    public FeedTheMindSpellBack1() {
        // Discard this card unless you lose 2 Sanity.
        addSpellEffect("0-2", new SpellEffectImpl("Casting the spell, you open yourself to terrible knowledge. Your mind is filled with gibberish, repeating endlessly."));
        // The chosen investigator may improve 1 additional skill of his choice.
        addSpellEffect("3+", new SpellEffectImpl("You manipulate memories and potential knowledge with a practiced ease."));
    }
}
