package sk.sivak.eldritchhorror.core.constants.spell.stormofspirits;

import sk.sivak.eldritchhorror.core.constants.spell.AbstractSpellBack;
import sk.sivak.eldritchhorror.core.constants.spell.SpellEffectImpl;

public class StormOfSpiritsSpellBack1 extends AbstractSpellBack {

    public StormOfSpiritsSpellBack1() {
        // If you rolled any 1's, discard this card.
        addSpellEffect("0", new SpellEffectImpl("The spirits will not heed your call."));

        // Reduce the Monster's damage by 1 to a minimum of 1.
        addSpellEffect("1+", new SpellEffectImpl("The channeled spirits protect you from harm."));
    }
}
