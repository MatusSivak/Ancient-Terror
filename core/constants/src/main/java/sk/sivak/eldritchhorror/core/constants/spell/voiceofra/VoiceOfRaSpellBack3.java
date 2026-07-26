package sk.sivak.eldritchhorror.core.constants.spell.voiceofra;

import sk.sivak.eldritchhorror.core.constants.spell.AbstractSpellBack;
import sk.sivak.eldritchhorror.core.constants.spell.SpellEffectImpl;

public class VoiceOfRaSpellBack3 extends AbstractSpellBack {

    public VoiceOfRaSpellBack3() {
        // Discard this card unless you spend 1 Focus.
        addSpellEffect("0", new SpellEffectImpl("Only through intense concentration can you maintain the aura."));

        // Lose 1 Sanity.
        addSpellEffect("1-2", new SpellEffectImpl("Stress and fatigue wears at you."));

        // You may spend 1 Health or 1 Sanity to gain 1 Focus.
        addSpellEffect("3+", new SpellEffectImpl("You maintain your concentration at the cost of your own wellbeing."));
    }
}
