package sk.sivak.eldritchhorror.core.constants.spell.healingwords;

import sk.sivak.eldritchhorror.core.constants.spell.AbstractSpellBack;
import sk.sivak.eldritchhorror.core.constants.spell.SpellEffectImpl;

public class HealingWordsSpellBack1 extends AbstractSpellBack {

    public HealingWordsSpellBack1() {
        // If you rolled any 1's, discard this card
        addSpellEffect("0", new SpellEffectImpl("The words of the incantation seem somehow foreign to you."));

        // No additional effect.
        addSpellEffect("1+", new SpellEffectImpl("Your body and mind become whole again."));

    }
}
