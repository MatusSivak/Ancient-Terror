package sk.sivak.eldritchhorror.core.constants.spell.conjuration;

import sk.sivak.eldritchhorror.core.constants.spell.AbstractSpellBack;
import sk.sivak.eldritchhorror.core.constants.spell.SpellEffectImpl;

public class ConjurationSpellBack1 extends AbstractSpellBack {

    public ConjurationSpellBack1() {
        // If you did not roll any 4's, discard this card.
        addSpellEffect("0", new SpellEffectImpl("What could you have possibly forgotten? Why didn't the spell work?"));
        // Lose 1 Sanity.
        addSpellEffect("1-3", new SpellEffectImpl("For a moment, your mind is exposed to a powerful intelligence, dwelling between worlds."));
        // Gain 1 additional Item or Trinket Asset from the reserve.
        addSpellEffect("4+", new SpellEffectImpl("The magic is powerful and beyond your expectations."));
    }
}
