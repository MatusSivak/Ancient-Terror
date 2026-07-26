package sk.sivak.eldritchhorror.core.constants.spell.wither;

import sk.sivak.eldritchhorror.core.constants.spell.AbstractSpellBack;
import sk.sivak.eldritchhorror.core.constants.spell.SpellEffectImpl;

public class WitherSpellBack2 extends AbstractSpellBack {

    public WitherSpellBack2() {
        // Discard this card unless you gain an Internal Injury Condition.
        addSpellEffect("0", new SpellEffectImpl("The words must have been wrong. You suffer the damage you had hoped to inflict."));

        // Lose 1 Sanity.
        addSpellEffect("1", new SpellEffectImpl("The spell compacts and twists the target's body."));

        // You may reroll 1 die during the Strength test.
        addSpellEffect("2+", new SpellEffectImpl("The magic ripples and surges through the target once more."));
    }
}
