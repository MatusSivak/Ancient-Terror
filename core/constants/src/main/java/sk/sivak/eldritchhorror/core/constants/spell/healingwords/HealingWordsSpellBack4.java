package sk.sivak.eldritchhorror.core.constants.spell.healingwords;

import sk.sivak.eldritchhorror.core.constants.spell.AbstractSpellBack;
import sk.sivak.eldritchhorror.core.constants.spell.SpellEffectImpl;

public class HealingWordsSpellBack4 extends AbstractSpellBack {

    public HealingWordsSpellBack4() {
        // That investigator loses 1 Health and 1 Sanity.
        addSpellEffect("0", new SpellEffectImpl("The magic does nothing but hinder your rest, adding to your fatigue and dread."));

        // No additional effect.
        addSpellEffect("1-2", new SpellEffectImpl("Your slumber is deep and refreshing."));

        // That investigator may discard a Cursed Condition.
        addSpellEffect("3+", new SpellEffectImpl("In your dreams, countless cats watch over you, relieving you of your lingering doubts."));
    }
}
