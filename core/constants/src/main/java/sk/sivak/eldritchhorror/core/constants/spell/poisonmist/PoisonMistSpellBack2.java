package sk.sivak.eldritchhorror.core.constants.spell.poisonmist;

import sk.sivak.eldritchhorror.core.constants.spell.AbstractSpellBack;
import sk.sivak.eldritchhorror.core.constants.spell.SpellEffectImpl;

public class PoisonMistSpellBack2 extends AbstractSpellBack {

    public PoisonMistSpellBack2() {
        // If you rolled any 1's, discard this card.
        addSpellEffect("0", new SpellEffectImpl("The ritual seems somehow alien to you, and you are unsure if you have spoken the correct words."));
        // Lose 1 Sanity.
        addSpellEffect("1+", new SpellEffectImpl("Watching the cloud smother the living is difficult to watch."));
    }
}
