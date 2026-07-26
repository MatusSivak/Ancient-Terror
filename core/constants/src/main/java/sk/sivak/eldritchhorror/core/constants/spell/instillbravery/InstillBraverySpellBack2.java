package sk.sivak.eldritchhorror.core.constants.spell.instillbravery;

import sk.sivak.eldritchhorror.core.constants.spell.AbstractSpellBack;
import sk.sivak.eldritchhorror.core.constants.spell.SpellEffectImpl;

public class InstillBraverySpellBack2 extends AbstractSpellBack {

    public InstillBraverySpellBack2() {
        // The chosen investigator loses 1 Sanity unless he gains an Hallucinations Condition.
        addSpellEffect("0", new SpellEffectImpl("The magic warps your mind and thoughts until you can no longer tell reality from delusion."));

        // No additional effect.
        addSpellEffect("1-2", new SpellEffectImpl("The spell prevents any lingering effects of the traumatic experience."));

        // Prevent that investigator from losing Sanity up to your test result instead.
        addSpellEffect("3+", new SpellEffectImpl("The spell inspires and invigorates."));
    }
}
