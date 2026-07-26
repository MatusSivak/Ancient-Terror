package sk.sivak.eldritchhorror.core.constants.spell.healingwords;

import sk.sivak.eldritchhorror.core.constants.spell.AbstractSpellBack;
import sk.sivak.eldritchhorror.core.constants.spell.SpellEffectImpl;

public class HealingWordsSpellBack2 extends AbstractSpellBack {

    public HealingWordsSpellBack2() {
        // That investigator loses 1 Health.
        addSpellEffect("0", new SpellEffectImpl("The magic is strange and does not allow wounds to heal."));

        // No additional effect.
        addSpellEffect("1", new SpellEffectImpl("The magic weaves itself with your dreams, affording you a restful night's sleep."));

        // That investigator may discard 1 Illness or Injury Condition
        addSpellEffect("2+", new SpellEffectImpl("Your body is cleansed and mended."));

    }
}
