package sk.sivak.eldritchhorror.core.constants.spell.fleshward;

import sk.sivak.eldritchhorror.core.constants.spell.AbstractSpellBack;
import sk.sivak.eldritchhorror.core.constants.spell.SpellEffectImpl;

public class FleshWardSpellBack4 extends AbstractSpellBack {

    public FleshWardSpellBack4() {
        // If you rolled any 1's, discard this card.
        addSpellEffect("0", new SpellEffectImpl("The incantation slips from your memory, and you are unsure if it will return."));

        // That investigator loses 1 Sanity.
        addSpellEffect("1-2", new SpellEffectImpl("Flesh and bone writhe and reform as your wounds are mended."));

        // Prevent that investigator from losing up to 4 Health instead.
        addSpellEffect("3+", new SpellEffectImpl("The magic wards you from physical harm, rendering you nearly immortal."));
    }
}
