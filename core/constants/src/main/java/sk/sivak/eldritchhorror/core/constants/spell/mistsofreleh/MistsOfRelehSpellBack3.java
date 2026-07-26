package sk.sivak.eldritchhorror.core.constants.spell.mistsofreleh;

import sk.sivak.eldritchhorror.core.constants.spell.AbstractSpellBack;
import sk.sivak.eldritchhorror.core.constants.spell.SpellEffectImpl;

public class MistsOfRelehSpellBack3 extends AbstractSpellBack {

    public MistsOfRelehSpellBack3() {
        // If you rolled any 1's, discard this card.
        addSpellEffect("0", new SpellEffectImpl("You utter the words incorrectly."));

        // Lose 1 Sanity.
        addSpellEffect("1+", new SpellEffectImpl("The mist fills the air and clouds your mind."));
    }
}
