package sk.sivak.eldritchhorror.core.constants.spell.conjuration;

import sk.sivak.eldritchhorror.core.constants.spell.AbstractSpellBack;
import sk.sivak.eldritchhorror.core.constants.spell.SpellEffectImpl;

public class ConjurationSpellBack3 extends AbstractSpellBack {

    public ConjurationSpellBack3() {
        // Discard this card and each Item possession you have.
        addSpellEffect("0", new SpellEffectImpl("The magic surges and tears through solid matter."));
        // Lose 1 Sanity.
        addSpellEffect("1-3", new SpellEffectImpl("For a moment, your mind melds with the object of your desire."));
        // You may gain 1 Artifact instead of gaining an Asset.
        addSpellEffect("4+", new SpellEffectImpl("The ritual proves stronger than you ever imagined."));
    }
}
