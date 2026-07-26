package sk.sivak.eldritchhorror.core.constants.spell.instillbravery;

import sk.sivak.eldritchhorror.core.constants.spell.AbstractSpellBack;
import sk.sivak.eldritchhorror.core.constants.spell.SpellEffectImpl;

public class InstillBraverySpellBack1 extends AbstractSpellBack {

    public InstillBraverySpellBack1() {
        // If you did not roll any 4's, discard this card.
        addSpellEffect("0", new SpellEffectImpl("Your malformed magic lashes against your mind, and you fear your memories have been altered."));

        // Lose 1 Health.
        addSpellEffect("1+", new SpellEffectImpl("Your incantation inspires courage in the face of madness, but at the cost of your own blood."));
    }
}
