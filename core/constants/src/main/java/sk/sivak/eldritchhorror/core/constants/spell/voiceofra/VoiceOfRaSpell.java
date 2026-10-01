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
        return "Once per round, in the Action Phase:\n" +
                "1 Health and 1 Sanity → 1 extra action.\n" +
                "\n" +
                "RECKONING: Test Lore, then flip this card.";
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
