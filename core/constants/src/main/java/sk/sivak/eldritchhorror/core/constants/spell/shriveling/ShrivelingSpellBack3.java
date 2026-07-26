package sk.sivak.eldritchhorror.core.constants.spell.shriveling;

import sk.sivak.eldritchhorror.core.constants.spell.AbstractSpellBack;
import sk.sivak.eldritchhorror.core.constants.spell.SpellEffectImpl;

public class ShrivelingSpellBack3 extends AbstractSpellBack {

    public ShrivelingSpellBack3() {
        // Discard this card unless you gain a Poisoned Condition.
        addSpellEffect("0", new SpellEffectImpl("The magic backfires, assailing your flesh with blisters and pox."));
        // Lose 1 Health.
        addSpellEffect("1-2", new SpellEffectImpl("The harmful magic lingers in your body."));
        // The chosen Monster loses 4 Health instead.
        addSpellEffect("3+", new SpellEffectImpl("Your target's flesh writhes and boils, shedding off in a molten husk."));
    }
}
