package sk.sivak.eldritchhorror.core.constants.spell.shriveling;

import sk.sivak.eldritchhorror.core.constants.spell.AbstractSpellBack;
import sk.sivak.eldritchhorror.core.constants.spell.SpellEffectImpl;

public class ShrivelingSpellBack1 extends AbstractSpellBack {

    public ShrivelingSpellBack1() {
        // Discard this card unless you lose 2 Sanity.
        addSpellEffect("0-1", new SpellEffectImpl("The ritual echoes in your head until it becomes gibberish."));
        // No additional effect.
        addSpellEffect("2", new SpellEffectImpl("The enemy's flesh boils and peels away."));
        // The chosen Monster loses Health equal to your test result instead.
        addSpellEffect("3+", new SpellEffectImpl("Flames and thick smoke envelop your target."));
    }
}
