package sk.sivak.eldritchhorror.core.constants.spell.shriveling;

import sk.sivak.eldritchhorror.core.constants.spell.AbstractSpellBack;
import sk.sivak.eldritchhorror.core.constants.spell.SpellEffectImpl;

public class ShrivelingSpellBack2 extends AbstractSpellBack {

    public ShrivelingSpellBack2() {
        // Lose 2 Health.
        addSpellEffect("0", new SpellEffectImpl("The spell backfires, searing your hands."));
        // Lose 1 Sanity.
        addSpellEffect("1", new SpellEffectImpl("The distinct smell of burning flesh lets you know the spell has been cast correctly."));
        // Each other Monster on your space also loses 2 Health.
        addSpellEffect("2+", new SpellEffectImpl("The magic spreads, and the burning flesh peels from your enemies' bodies."));
    }
}
