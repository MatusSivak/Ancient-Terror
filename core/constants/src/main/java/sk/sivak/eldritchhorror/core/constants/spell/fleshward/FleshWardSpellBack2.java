package sk.sivak.eldritchhorror.core.constants.spell.fleshward;

import sk.sivak.eldritchhorror.core.constants.spell.AbstractSpellBack;
import sk.sivak.eldritchhorror.core.constants.spell.SpellEffectImpl;

public class FleshWardSpellBack2 extends AbstractSpellBack {

    public FleshWardSpellBack2() {
        // The chosen investigator loses 1 Health unless he gains an Internal Injury Condition.
        addSpellEffect("0", new SpellEffectImpl("Something inside your chest shifts painfully."));

        // No additional effect.
        addSpellEffect("1-2", new SpellEffectImpl("The spell causes skin to writhe and reform like a living being."));

        // Prevent that investigator from losing Health up to your test result instead.
        addSpellEffect("3+", new SpellEffectImpl("Flesh and bone mend themselves and harden against attack."));
    }
}
