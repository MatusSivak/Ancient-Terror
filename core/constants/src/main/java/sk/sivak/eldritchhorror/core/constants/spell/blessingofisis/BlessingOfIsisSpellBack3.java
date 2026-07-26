package sk.sivak.eldritchhorror.core.constants.spell.blessingofisis;

import sk.sivak.eldritchhorror.core.constants.spell.AbstractSpellBack;
import sk.sivak.eldritchhorror.core.constants.spell.SpellEffectImpl;

public class BlessingOfIsisSpellBack3 extends AbstractSpellBack {

    public BlessingOfIsisSpellBack3() {
        // Lose 1 Sanity.
        addSpellEffect("0-1", new SpellEffectImpl("The voice of the great being Isis echoes in your mind."));
        // The chosen investigator gains 1 Clue.
        addSpellEffect("2+", new SpellEffectImpl("Visions of distant places and times flood your mind."));
    }
}
