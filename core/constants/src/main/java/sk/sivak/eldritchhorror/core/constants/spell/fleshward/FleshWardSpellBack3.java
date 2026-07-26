package sk.sivak.eldritchhorror.core.constants.spell.fleshward;

import sk.sivak.eldritchhorror.core.constants.spell.AbstractSpellBack;
import sk.sivak.eldritchhorror.core.constants.spell.SpellEffectImpl;

public class FleshWardSpellBack3 extends AbstractSpellBack {

    public FleshWardSpellBack3() {
        // That investigator gains 1 Illness Condition.
        addSpellEffect("0", new SpellEffectImpl("The malformed magic inflicts the subject with a terrible illness."));

        // Lose 1 Sanity.
        addSpellEffect("1", new SpellEffectImpl("Stitching together flesh and bone takes a toll on your fragile mind."));

        // No additional effect.
        addSpellEffect("2+", new SpellEffectImpl("Your flesh hardens and repels pain and wounds with ease."));
    }
}
