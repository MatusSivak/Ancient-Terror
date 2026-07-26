package sk.sivak.eldritchhorror.core.constants.spell.intervene;

import sk.sivak.eldritchhorror.core.constants.spell.AbstractSpellBack;
import sk.sivak.eldritchhorror.core.constants.spell.SpellEffectImpl;

public class InterveneSpellBack2 extends AbstractSpellBack {

    public InterveneSpellBack2() {
        // That investigator loses 2 Sanity unless you discard this card.
        addSpellEffect("0-1", new SpellEffectImpl("The spirits you channel through your companion give him strength but grate the mind."));

        //  That investigator loses 1 Sanity and may reroll up to 2 dice when resolving the Strength test during that encounter.
        addSpellEffect("2+", new SpellEffectImpl("The spirits are difficult to fathom, but they grant great power."));
    }
}
