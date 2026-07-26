package sk.sivak.eldritchhorror.core.constants.spell.plumbthevoid;

import sk.sivak.eldritchhorror.core.constants.spell.AbstractSpellBack;
import sk.sivak.eldritchhorror.core.constants.spell.SpellEffectImpl;

public class PlumbTheVoidSpellBack2 extends AbstractSpellBack {

    public PlumbTheVoidSpellBack2() {
        // Discard this card unless you gain an Amnesia Condition.
        addSpellEffect("0-1", new SpellEffectImpl("You remember that a swirling passage to an unknown world opened before you, " +
                "but did you travel through it? If you did, where did you go?"));

        // No additional effect.
        addSpellEffect("2+", new SpellEffectImpl("You bend the fabric of space to your will, transporting the subject across the globe."));
    }
}
