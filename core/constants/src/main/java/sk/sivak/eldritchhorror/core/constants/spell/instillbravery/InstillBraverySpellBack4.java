package sk.sivak.eldritchhorror.core.constants.spell.instillbravery;

import sk.sivak.eldritchhorror.core.constants.spell.AbstractSpellBack;
import sk.sivak.eldritchhorror.core.constants.spell.SpellEffectImpl;

public class InstillBraverySpellBack4 extends AbstractSpellBack {

    public InstillBraverySpellBack4() {
        // If you rolled any 1's, discard this card.
        addSpellEffect("0", new SpellEffectImpl("Fear overwhelms you and shatters the weak magic."));

        // That investigator loses 1 Health.
        addSpellEffect("1-2", new SpellEffectImpl("The magic binds itself to your body, preventing you from fleeing."));

        // Prevent that investigator from losing up to 4 Sanity instead.
        addSpellEffect("3+", new SpellEffectImpl("The magic alters your mind, allowing you to stand against your greatest fears."));
    }
}
