package sk.sivak.eldritchhorror.core.constants.spell.conjuration;

import sk.sivak.eldritchhorror.core.constants.spell.AbstractSpellBack;
import sk.sivak.eldritchhorror.core.constants.spell.SpellEffectImpl;

public class ConjurationSpellBack2 extends AbstractSpellBack {

    public ConjurationSpellBack2() {
        // Discard this card unless you discard 1 Item Asset.
        addSpellEffect("0", new SpellEffectImpl("You can feel that the spell has not worked the way you hoped."));
        // Lose 1 Sanity.
        addSpellEffect("1", new SpellEffectImpl("Something unnatural tries to pull the object away from you."));
        // You may gain any number of Item or Trinket Assets from the reserve with total value equal to or less than your test result.
        addSpellEffect("2+", new SpellEffectImpl("All of your wishes materialize before your eyes."));
    }
}
