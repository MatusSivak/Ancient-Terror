package sk.sivak.eldritchhorror.core.constants.spell.poisonmist;

import sk.sivak.eldritchhorror.core.constants.spell.AbstractSpellBack;
import sk.sivak.eldritchhorror.core.constants.spell.SpellEffectImpl;

public class PoisonMistSpellBack1 extends AbstractSpellBack {

    public PoisonMistSpellBack1() {
        // Gain a Poisoned Condition.
        addSpellEffect("0", new SpellEffectImpl("The mist blows back and surrounds you."));
        // Lose 1 Health.
        addSpellEffect("1-2", new SpellEffectImpl("The harmful magic surges through you during the ritual."));
        // Add 2 successes to your test result.
        addSpellEffect("3+", new SpellEffectImpl("The deadly cloud of gas snuffs out all life it touches."));
    }
}
