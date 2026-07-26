package sk.sivak.eldritchhorror.core.constants.spell.voiceofra;

import sk.sivak.eldritchhorror.core.constants.spell.AbstractSpellBack;
import sk.sivak.eldritchhorror.core.constants.spell.SpellEffectImpl;

public class VoiceOfRaSpellBack2 extends AbstractSpellBack {

    public VoiceOfRaSpellBack2() {
        // Lose 1 Health and gain 1 Injury Condition unless you discard this card.
        addSpellEffect("0", new SpellEffectImpl("The strain of maintaining the magic is too much for your body."));

        // Lose 1 Health.
        addSpellEffect("1", new SpellEffectImpl("The voice demands a payment of blood."));

        // You may spend 1 Health to recover 1 Sanity.
        addSpellEffect("2+", new SpellEffectImpl("The voice offers you mental endurance for a price."));
    }
}
