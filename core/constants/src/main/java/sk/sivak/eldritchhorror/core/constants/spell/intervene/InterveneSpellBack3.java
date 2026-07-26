package sk.sivak.eldritchhorror.core.constants.spell.intervene;

import sk.sivak.eldritchhorror.core.constants.spell.AbstractSpellBack;
import sk.sivak.eldritchhorror.core.constants.spell.SpellEffectImpl;

public class InterveneSpellBack3 extends AbstractSpellBack {

    public InterveneSpellBack3() {
        // That investigator gains 1 Injury Condition unless you discard this card.
        addSpellEffect("0", new SpellEffectImpl("You project your spirit to assist but only manage to hinder the situation."));

        // That investigator loses 1 Sanity.
        addSpellEffect("1-2", new SpellEffectImpl("The spirits wrack the mind."));

        //  That investigator rolls 1 additional die when resolving the Strength test during the encounter.
        addSpellEffect("3+", new SpellEffectImpl("The spirits heed your every beck and call."));
    }
}
