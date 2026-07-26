package sk.sivak.eldritchhorror.core.constants.spell.voiceofra;

import sk.sivak.eldritchhorror.core.constants.spell.AbstractSpellInfo;
import sk.sivak.eldritchhorror.core.constants.spell.SpellId;
import sk.sivak.eldritchhorror.core.constants.spell.SpellTrait;

public class VoiceOfRaSpell extends AbstractSpellInfo {
    public VoiceOfRaSpell() {
        traits.add(SpellTrait.GLAMOUR);
    }

    @Override
    public String getName() {
        return "Voice of Ra";
    }

    @Override
    public String getDescription() {
        return "Once per round,\n" +
                "during the Action Phase,\n" +
                "you may spend 1 Health and 1 Sanity\n" +
                "to perform 1 additional action.\n\n" +
                "RECKONING: Test Lore and flip this card.";
    }

    @Override
    public SpellId getId() {
        return SpellId.VOICE_OF_RA;
    }

    @Override
    public boolean hasReckoning() {
        return true;
    }
}
