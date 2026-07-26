package sk.sivak.eldritchhorror.core.constants.spell.intervene;

import sk.sivak.eldritchhorror.core.constants.spell.AbstractSpellBack;
import sk.sivak.eldritchhorror.core.constants.spell.SpellEffectImpl;

public class InterveneSpellBack1 extends AbstractSpellBack {

    public InterveneSpellBack1() {
        // If you rolled any 1's, discard this card.
        addSpellEffect("0", new SpellEffectImpl("For the moment, you cannot remember the words of the incantation."));

        //  Lose 1 Sanity.
        addSpellEffect("1-2", new SpellEffectImpl("You have a vision of our companion's battle and fear for his safety."));

        //  No additional effect.
        addSpellEffect("3+", new SpellEffectImpl("You see the battle clearly, as if standing there yourself."));
    }
}
