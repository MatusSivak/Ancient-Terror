package sk.sivak.eldritchhorror.core.constants.spell.mistsofreleh;

import sk.sivak.eldritchhorror.core.constants.spell.AbstractSpellBack;
import sk.sivak.eldritchhorror.core.constants.spell.SpellEffectImpl;

public class MistsOfRelehSpellBack1 extends AbstractSpellBack {

    public MistsOfRelehSpellBack1() {
        // Lose 1 Health and 1 Sanity.
        addSpellEffect("0-1", new SpellEffectImpl("A thick, black fog engulfs you, choking your lungs and stinging your eyes."));

        // No additional effects.
        addSpellEffect("2+", new SpellEffectImpl("Your body and mind become whole again."));

    }
}
