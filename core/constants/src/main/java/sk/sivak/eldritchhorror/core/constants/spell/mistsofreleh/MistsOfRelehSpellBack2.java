package sk.sivak.eldritchhorror.core.constants.spell.mistsofreleh;

import sk.sivak.eldritchhorror.core.constants.spell.AbstractSpellBack;
import sk.sivak.eldritchhorror.core.constants.spell.SpellEffectImpl;

public class MistsOfRelehSpellBack2 extends AbstractSpellBack {

    public MistsOfRelehSpellBack2() {
        // Discard this card unless you gain a Hallucinations Condition.
        addSpellEffect("0", new SpellEffectImpl("You see a monstrous form vanish into the fog. Whatever it was, you fear it may return the next time you recite the incantation."));

        // Lose 1 Sanity.
        addSpellEffect("1+", new SpellEffectImpl("Although you are hidden by the darkness, you feel unnatural tendrils of fog wrapping around your body."));

    }
}
