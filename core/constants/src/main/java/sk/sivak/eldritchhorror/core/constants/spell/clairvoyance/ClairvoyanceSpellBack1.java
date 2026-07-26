package sk.sivak.eldritchhorror.core.constants.spell.clairvoyance;

import sk.sivak.eldritchhorror.core.constants.spell.AbstractSpellBack;
import sk.sivak.eldritchhorror.core.constants.spell.SpellEffectImpl;

public class ClairvoyanceSpellBack1 extends AbstractSpellBack {

    public ClairvoyanceSpellBack1() {
        // Discard this card unless you gain a Paranoia Condition.
        addSpellEffect("0", new SpellEffectImpl("Opening your mind wreaks havoc on your psyche."));
        // No additional effect.
        addSpellEffect("1-2", new SpellEffectImpl("Your mind travels across the world, and the knowledge becomes clear."));
        // You may roll 1 additional die when resolving tests during the Research Encounter.
        addSpellEffect("3+", new SpellEffectImpl("The journey becomes clear to you, and the answer seems so simple."));
    }
}
