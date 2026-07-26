package sk.sivak.eldritchhorror.core.constants.spell.shriveling;

import sk.sivak.eldritchhorror.core.constants.spell.AbstractSpellBack;
import sk.sivak.eldritchhorror.core.constants.spell.SpellEffectImpl;

public class ShrivelingSpellBack4 extends AbstractSpellBack {

    public ShrivelingSpellBack4() {
        // Immediately resolve a Combat Encounter against each Monster on your space in the order of your choice.
        addSpellEffect("0-1", new SpellEffectImpl("Your magic calls attention to your location."));
        //No additional effect.
        addSpellEffect("2+", new SpellEffectImpl("The beast before you cowers and coils up, shrinking and growing weak."));
    }
}
