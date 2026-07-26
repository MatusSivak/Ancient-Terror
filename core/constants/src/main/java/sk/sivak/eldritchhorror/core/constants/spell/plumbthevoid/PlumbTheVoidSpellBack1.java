package sk.sivak.eldritchhorror.core.constants.spell.plumbthevoid;

import sk.sivak.eldritchhorror.core.constants.spell.AbstractSpellBack;
import sk.sivak.eldritchhorror.core.constants.spell.SpellEffectImpl;

public class PlumbTheVoidSpellBack1 extends AbstractSpellBack {

    public PlumbTheVoidSpellBack1() {
        // The chosen investigator loses 3 Health
        addSpellEffect("0", new SpellEffectImpl("The portal closes abruptly, nearly severing you in half."));

        // Lose 1 Sanity
        addSpellEffect("1-2", new SpellEffectImpl("The ritual requires an exhausting amount of concentration."));

        // You may perform an additional action
        addSpellEffect("3+", new SpellEffectImpl("More than just warping space, you also manipulate time."));
    }
}
