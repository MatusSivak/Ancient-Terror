package sk.sivak.eldritchhorror.core.constants.spell.clairvoyance;

import sk.sivak.eldritchhorror.core.constants.spell.AbstractSpellBack;
import sk.sivak.eldritchhorror.core.constants.spell.SpellEffectImpl;

public class ClairvoyanceSpellBack3 extends AbstractSpellBack {

    public ClairvoyanceSpellBack3() {
        // If you rolled any 1's, discard the nearest Clue on the game board.
        addSpellEffect("0", new SpellEffectImpl("You unknowingly call upon a wrathful spirit that destroys the target of your scrying."));
        // Roll 1 less die when resolving tests during the Research Encounter.
        addSpellEffect("1-2", new SpellEffectImpl("The visions are clouded and murky."));
        // No additional effect.
        addSpellEffect("3+", new SpellEffectImpl("The visions become clear as if you are there in the flesh."));
    }
}
