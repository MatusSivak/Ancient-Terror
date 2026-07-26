package sk.sivak.eldritchhorror.core.constants.spell.healingwords;

import sk.sivak.eldritchhorror.core.constants.spell.AbstractSpellBack;
import sk.sivak.eldritchhorror.core.constants.spell.SpellEffectImpl;

public class HealingWordsSpellBack3 extends AbstractSpellBack {

    public HealingWordsSpellBack3() {
        // That investigator loses 1 Sanity.
        addSpellEffect("0", new SpellEffectImpl("You dream of savage beasts and terrible horrors."));

        // No additional effect.
        addSpellEffect("1", new SpellEffectImpl("A spirit watches over your sleep, protecting you from further harm."));

        // That investigator may discard 1 Madness Condition.
        addSpellEffect("2+", new SpellEffectImpl("You dream of a faraway paradise where your mind can be at ease."));

    }
}
