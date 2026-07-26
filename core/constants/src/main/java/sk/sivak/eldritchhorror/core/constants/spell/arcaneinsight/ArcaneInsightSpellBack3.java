package sk.sivak.eldritchhorror.core.constants.spell.arcaneinsight;

import sk.sivak.eldritchhorror.core.constants.spell.AbstractSpellBack;
import sk.sivak.eldritchhorror.core.constants.spell.SpellEffectImpl;

public class ArcaneInsightSpellBack3 extends AbstractSpellBack {

    public ArcaneInsightSpellBack3() {
        // That investigator discards all of his Clues unless he has a Tome Possession.
        addSpellEffect("0", new SpellEffectImpl("The arcane backlash tears at your mind and the focus of your ritual."));
        // Lose 1 Health.
        addSpellEffect("1-2", new SpellEffectImpl("Achieving ultimate knowledge requires only a small sacrifice"));
        // If that investigator has a Tome possession, he gains 1 additional Clue.
        addSpellEffect("3+", new SpellEffectImpl("The knowledge contained in the pages before you becomes clear."));
    }
}
