package sk.sivak.eldritchhorror.core.constants.spell.clairvoyance;

import sk.sivak.eldritchhorror.core.constants.spell.AbstractSpellBack;
import sk.sivak.eldritchhorror.core.constants.spell.SpellEffectImpl;

public class ClairvoyanceSpellBack4 extends AbstractSpellBack {

    public ClairvoyanceSpellBack4() {
        // If you rolled any 1's, discard this card.
        addSpellEffect("0", new SpellEffectImpl("You can see nothing but darkness."));
        // Lose 1 Sanity unless you gain a Paranoia Condition.
        addSpellEffect("1", new SpellEffectImpl("You peer through the eyes of vengeful spirits that whisper horrible secrets to you."));
        // Spawn 1 Clue.
        addSpellEffect("2+", new SpellEffectImpl("As you scry, your sight falls upon a previously hidden source of knowledge."));
    }
}
