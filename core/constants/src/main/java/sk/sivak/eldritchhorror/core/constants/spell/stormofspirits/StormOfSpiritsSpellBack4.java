package sk.sivak.eldritchhorror.core.constants.spell.stormofspirits;

import sk.sivak.eldritchhorror.core.constants.spell.AbstractSpellBack;
import sk.sivak.eldritchhorror.core.constants.spell.SpellEffectImpl;

public class StormOfSpiritsSpellBack4 extends AbstractSpellBack {

    public StormOfSpiritsSpellBack4() {
        // Lose 1 Health and 1 Sanity unless you discard this card.
        addSpellEffect("0", new SpellEffectImpl("You call upon evil spirits which turn against you."));

        // If you defeat the Monster, recover 1 Health or 1 Sanity.
        addSpellEffect("1+", new SpellEffectImpl("The spirits siphon the creature's lifeforce, feeding it to you."));
    }
}
