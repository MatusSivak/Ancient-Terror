package sk.sivak.eldritchhorror.core.constants.spell.arcaneinsight;

import sk.sivak.eldritchhorror.core.constants.spell.AbstractSpellBack;
import sk.sivak.eldritchhorror.core.constants.spell.SpellEffectImpl;

public class ArcaneInsightSpellBack4 extends AbstractSpellBack {

    public ArcaneInsightSpellBack4() {
        // That investigator loses 2 Sanity unless he has a Tome possession.
        addSpellEffect("0-2", new SpellEffectImpl("All knowledge of all times and places assaults your feeble mind, shredding your previous perceptions of reality."));
        // No additional effect.
        addSpellEffect("3+", new SpellEffectImpl("Your understanding of the arcane knowledge is unyielding."));
    }
}
