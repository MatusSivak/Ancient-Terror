package sk.sivak.eldritchhorror.core.constants.spell.wither;

import sk.sivak.eldritchhorror.core.constants.spell.AbstractSpellBack;
import sk.sivak.eldritchhorror.core.constants.spell.SpellEffectImpl;

public class WitherSpellBack4 extends AbstractSpellBack {

    public WitherSpellBack4() {
        // Gain 1 Illness Condition.
        addSpellEffect("0", new SpellEffectImpl("The magic backfires, infecting your lungs and invading your blood stream."));

        // Lose 1 Health.
        addSpellEffect("1", new SpellEffectImpl("Completing the incantation requires a blood sacrifice."));

        // Reduce the Monster's damage by 1 to a minimum of 1.
        addSpellEffect("2+", new SpellEffectImpl("Your target's body withers into a weakened, malformed shape."));
    }
}
