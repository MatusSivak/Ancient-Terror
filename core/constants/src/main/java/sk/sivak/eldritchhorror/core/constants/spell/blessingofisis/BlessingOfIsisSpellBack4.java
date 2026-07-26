package sk.sivak.eldritchhorror.core.constants.spell.blessingofisis;

import sk.sivak.eldritchhorror.core.constants.spell.AbstractSpellBack;
import sk.sivak.eldritchhorror.core.constants.spell.SpellEffectImpl;

public class BlessingOfIsisSpellBack4 extends AbstractSpellBack {

    public BlessingOfIsisSpellBack4() {
        // If you rolled any 1's, discard this card.
        addSpellEffect("0", new SpellEffectImpl("The words of the ritual escape you, and for a moment, you cannot recall them."));
        // The chosen investigator loses 1 Sanity.
        addSpellEffect("1", new SpellEffectImpl("A glowing spirit circles you, warding disaster."));
        // The chosen investigator recovers 1 Health and 1 Sanity.
        addSpellEffect("2+", new SpellEffectImpl("A warmth fills you and eases our pain and worries."));
    }
}
