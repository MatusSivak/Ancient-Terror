package sk.sivak.eldritchhorror.core.constants.spell.voiceofra;

import sk.sivak.eldritchhorror.core.constants.spell.AbstractSpellBack;
import sk.sivak.eldritchhorror.core.constants.spell.SpellEffectImpl;

public class VoiceOfRaSpellBack4 extends AbstractSpellBack {

    public VoiceOfRaSpellBack4() {
        // If you rolled any 1's, discard this card.
        addSpellEffect("0", new SpellEffectImpl("Suddenly the voice goes quiet. "));

        // Lose 1 Sanity.
        addSpellEffect("1-2", new SpellEffectImpl("The voice speaks to you constantly, interrupting your rest and causing you to become fatigued."));

        // You may spend 1 Health and 1 Sanity to immediately perform 1 additional action.
        addSpellEffect("3+", new SpellEffectImpl("The voice presses you onward despite your fatigue. "));
    }
}
