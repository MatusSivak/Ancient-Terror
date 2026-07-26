package sk.sivak.eldritchhorror.core.constants.spell.plumbthevoid;

import sk.sivak.eldritchhorror.core.constants.spell.AbstractSpellBack;
import sk.sivak.eldritchhorror.core.constants.spell.SpellEffectImpl;

public class PlumbTheVoidSpellBack3 extends AbstractSpellBack {

    public PlumbTheVoidSpellBack3() {
        // The chosen investigator loses 1 Sanity and moves to a random space.
        addSpellEffect("0", new SpellEffectImpl("You misspeak the words, opening the portal in the wrong place."));

        // The chosen investigator loses 1 Sanity.
        addSpellEffect("1+", new SpellEffectImpl("Traveling through the void takes a toll on your mind. "));
    }
}
