package sk.sivak.eldritchhorror.core.constants.spell.conjuration;

import sk.sivak.eldritchhorror.core.constants.spell.AbstractSpellBack;
import sk.sivak.eldritchhorror.core.constants.spell.SpellEffectImpl;

public class ConjurationSpellBack4 extends AbstractSpellBack {

    public ConjurationSpellBack4() {
        // Discard this card unless you lose 3 Health.
        addSpellEffect("0", new SpellEffectImpl("Your stutter causes the item to form from your own flesh and bone."));
        // Lose 1 Health.
        addSpellEffect("1", new SpellEffectImpl("A payment of blood is required to complete the ritual."));
        // No additional effect.
        addSpellEffect("2+", new SpellEffectImpl("Matter bends to your will and reshapes itself as you desire."));
    }
}
