package sk.sivak.eldritchhorror.core.constants.spell.stormofspirits;

import sk.sivak.eldritchhorror.core.constants.spell.AbstractSpellBack;
import sk.sivak.eldritchhorror.core.constants.spell.SpellEffectImpl;

public class StormOfSpiritsSpellBack2 extends AbstractSpellBack {

    public StormOfSpiritsSpellBack2() {
        // Lose 1 Sanity.
        addSpellEffect("0", new SpellEffectImpl("The spirits are restless and require intense mental discipline to control."));

        // If you defeat the Monster, you may spend 1 Sanity to gain 1 Clue.
        addSpellEffect("1+", new SpellEffectImpl("The spirits are able to steal the memories of your victim before its demise."));
    }
}
