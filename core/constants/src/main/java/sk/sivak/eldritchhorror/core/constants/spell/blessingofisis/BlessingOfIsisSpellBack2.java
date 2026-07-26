package sk.sivak.eldritchhorror.core.constants.spell.blessingofisis;

import sk.sivak.eldritchhorror.core.constants.spell.AbstractSpellBack;
import sk.sivak.eldritchhorror.core.constants.spell.SpellEffectImpl;

public class BlessingOfIsisSpellBack2 extends AbstractSpellBack {

    public BlessingOfIsisSpellBack2() {
        // Lose 1 Health.
        addSpellEffect("0-2", new SpellEffectImpl("Your chanting fills the air with ancient voices, and you fear the invocation has drained part of your life away."));
        // Each investigator on your space that does not have a Blessed Condition may gain a Blessed Condition.
        addSpellEffect("3+", new SpellEffectImpl("The goddess smiles upon your cause."));
    }
}
