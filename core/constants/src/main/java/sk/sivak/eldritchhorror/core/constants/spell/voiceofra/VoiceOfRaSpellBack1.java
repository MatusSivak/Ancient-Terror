package sk.sivak.eldritchhorror.core.constants.spell.voiceofra;

import sk.sivak.eldritchhorror.core.constants.spell.AbstractSpellBack;
import sk.sivak.eldritchhorror.core.constants.spell.SpellEffectImpl;

public class VoiceOfRaSpellBack1 extends AbstractSpellBack {

    public VoiceOfRaSpellBack1() {
        // Lose 1 Sanity and gain 1 Madness Condition unless you discard this card.
        addSpellEffect("0", new SpellEffectImpl("You hear the voice in your head at all times."));

        // Lose 1 Sanity.
        addSpellEffect("1", new SpellEffectImpl("The voice tells you terrible secrets."));

        // You may spend 1 Sanity to recover 1 Health.
        addSpellEffect("2+", new SpellEffectImpl("The voice offers you physical endurance for a price."));
    }
}
