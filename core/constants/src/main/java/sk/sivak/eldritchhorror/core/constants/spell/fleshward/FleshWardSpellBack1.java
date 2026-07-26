package sk.sivak.eldritchhorror.core.constants.spell.fleshward;

import sk.sivak.eldritchhorror.core.constants.spell.AbstractSpellBack;
import sk.sivak.eldritchhorror.core.constants.spell.SpellEffectImpl;

public class FleshWardSpellBack1 extends AbstractSpellBack {

    public FleshWardSpellBack1() {
        // If you did not roll any 4's, discard this card.
        addSpellEffect("0", new SpellEffectImpl("The spell's energy twists around you and locks your jaw shut as if it were forbidding you from speaking. You don't know if you will be allowed to speak those words again."));

        // Lose 1 Sanity.
        addSpellEffect("1+", new SpellEffectImpl("It is unpleasant to watch this transformation, even when it succeeds."));
    }
}
