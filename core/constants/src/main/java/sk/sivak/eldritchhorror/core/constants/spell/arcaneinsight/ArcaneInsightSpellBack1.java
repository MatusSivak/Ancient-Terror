package sk.sivak.eldritchhorror.core.constants.spell.arcaneinsight;

import sk.sivak.eldritchhorror.core.constants.spell.AbstractSpellBack;
import sk.sivak.eldritchhorror.core.constants.spell.SpellEffectImpl;

public class ArcaneInsightSpellBack1 extends AbstractSpellBack {

    public ArcaneInsightSpellBack1() {
        // Discard this card unless YOU have a Tome possession.
        addSpellEffect("0", new SpellEffectImpl("You are unable to grasp the arcane concepts laid out before you."));
        // Lose 1 Sanity.
        addSpellEffect("1-2", new SpellEffectImpl("Opening your mind to new knowledge proves stressful."));
        // If THAT investigator has a Tome possession, he gains 1 Spell.
        addSpellEffect("3+", new SpellEffectImpl("Knowledge flows freely from the cosmos into your own mind."));
    }
}
