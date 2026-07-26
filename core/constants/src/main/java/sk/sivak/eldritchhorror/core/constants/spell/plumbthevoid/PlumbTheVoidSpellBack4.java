package sk.sivak.eldritchhorror.core.constants.spell.plumbthevoid;

import sk.sivak.eldritchhorror.core.constants.spell.AbstractSpellBack;
import sk.sivak.eldritchhorror.core.constants.spell.SpellEffectImpl;

public class PlumbTheVoidSpellBack4 extends AbstractSpellBack {

    public PlumbTheVoidSpellBack4() {
        // The chosen investigator gains a Lost in Time and Space Condition.
        addSpellEffect("0", new SpellEffectImpl("The portal closes abruptly, trapping you in the space between worlds."));

        // Lose 1 Sanity.
        addSpellEffect("1-2", new SpellEffectImpl("Your mind leaves your body as the portal opens, and you fear you may become lost."));

        // No additional effect.
        addSpellEffect("3+", new SpellEffectImpl("The magic folds the fabric of reality, allowing you to step between continents with ease."));
    }
}
