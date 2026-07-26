package sk.sivak.eldritchhorror.core.constants.spell.clairvoyance;

import sk.sivak.eldritchhorror.core.constants.spell.AbstractSpellBack;
import sk.sivak.eldritchhorror.core.constants.spell.SpellEffectImpl;

public class ClairvoyanceSpellBack2 extends AbstractSpellBack {

    public ClairvoyanceSpellBack2() {
        // Discard the chosen Clue.
        addSpellEffect("0", new SpellEffectImpl("The knowledge gets lost in the void."));
        // Lose 1 Sanity.
        addSpellEffect("1-2", new SpellEffectImpl("The knowledge is too great for one mind to contain."));
        // Gain 1 Clue.
        addSpellEffect("3+", new SpellEffectImpl("Suddenly, the images and ideas coalesce into a new realization."));
    }
}
