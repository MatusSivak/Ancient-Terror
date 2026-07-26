package sk.sivak.eldritchhorror.core.constants.spell.mistsofreleh;

import sk.sivak.eldritchhorror.core.constants.spell.AbstractSpellBack;
import sk.sivak.eldritchhorror.core.constants.spell.SpellEffectImpl;

public class MistsOfRelehSpellBack4 extends AbstractSpellBack {

    public MistsOfRelehSpellBack4() {
        // Roll 1 less die when resolving tests during the Encounter Phase this round.
        addSpellEffect("0-1", new SpellEffectImpl("The mist clouds your vision and hinders your actions."));

        // No additional effect.
        addSpellEffect("2+", new SpellEffectImpl("Walking through the mist hides your presence from watchful eyes. "));
    }
}
