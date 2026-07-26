package sk.sivak.eldritchhorror.core.constants.spell.intervene;

import sk.sivak.eldritchhorror.core.constants.spell.AbstractSpellBack;
import sk.sivak.eldritchhorror.core.constants.spell.SpellEffectImpl;

public class InterveneSpellBack4 extends AbstractSpellBack {

    public InterveneSpellBack4() {
        // That investigator gains 1 Madness Condition unless you discard this card.
        addSpellEffect("0", new SpellEffectImpl("The spirits that flock to the scene are pestering and distracting."));

        // Lose 1 Health.
        addSpellEffect("1-2", new SpellEffectImpl("The strain of the battle is transferred to you."));

        //  Reduce the Monster's damage by 1 to a minimum of 1.
        addSpellEffect("3+", new SpellEffectImpl("Spirits surround the creature, binding its actions."));
    }
}
