package sk.sivak.eldritchhorror.core.constants.spell.arcaneinsight;

import sk.sivak.eldritchhorror.core.constants.spell.AbstractSpellBack;
import sk.sivak.eldritchhorror.core.constants.spell.SpellEffectImpl;

public class ArcaneInsightSpellBack2 extends AbstractSpellBack {

    public ArcaneInsightSpellBack2() {
        // THAT investigator gains an Amnesia Condition unless he has a Tome possession.
        addSpellEffect("0", new SpellEffectImpl("Suddenly, your mind is wiped blank."));
        // THAT investigator loses 1 Sanity for each Clue he has unless he has a Tome possession.
        addSpellEffect("1-2", new SpellEffectImpl("Knowledge floods your mind, straining your mortal brain."));
        // No additional effect.
        addSpellEffect("3+", new SpellEffectImpl("You easily pluck the secrets of time and space from the cosmos."));
    }
}
