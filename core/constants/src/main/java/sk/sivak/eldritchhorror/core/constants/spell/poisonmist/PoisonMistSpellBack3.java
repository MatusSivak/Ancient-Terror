package sk.sivak.eldritchhorror.core.constants.spell.poisonmist;

import sk.sivak.eldritchhorror.core.constants.spell.AbstractSpellBack;
import sk.sivak.eldritchhorror.core.constants.spell.SpellEffectImpl;

public class PoisonMistSpellBack3 extends AbstractSpellBack {

    public PoisonMistSpellBack3() {
        // Discard this card unless you lose 2 Health.
        addSpellEffect("0", new SpellEffectImpl("The mist hungers for your life force."));
        // Lose 1 Health.
        addSpellEffect("1", new SpellEffectImpl("Blood flows from your hands, turning into a noxious gas."));
        // Each Monster remaining on your space loses 1 Health.
        addSpellEffect("2+", new SpellEffectImpl("The poisonous cloud lingers, decaying the flesh of those unfortunate enough to become lost in it."));
    }
}
