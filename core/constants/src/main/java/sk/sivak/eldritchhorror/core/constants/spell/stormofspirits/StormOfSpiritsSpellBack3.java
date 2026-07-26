package sk.sivak.eldritchhorror.core.constants.spell.stormofspirits;

import sk.sivak.eldritchhorror.core.constants.spell.AbstractSpellBack;
import sk.sivak.eldritchhorror.core.constants.spell.SpellEffectImpl;

public class StormOfSpiritsSpellBack3 extends AbstractSpellBack {

    public StormOfSpiritsSpellBack3() {
        // Lose 1 Health
        addSpellEffect("0", new SpellEffectImpl("Controlling the spirits is only possible through blood sacrifice."));

        // For each Health you lose from the test, you may spend 1 Sanity. That Monster loses 1 Health for each Sanity you spend.
        addSpellEffect("1+", new SpellEffectImpl("You summon the spirits of retribution that grow only more vicious when their master is harmed."));
    }
}
