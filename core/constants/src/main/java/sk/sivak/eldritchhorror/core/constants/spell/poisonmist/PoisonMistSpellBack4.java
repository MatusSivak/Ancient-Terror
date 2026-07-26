package sk.sivak.eldritchhorror.core.constants.spell.poisonmist;

import sk.sivak.eldritchhorror.core.constants.spell.AbstractSpellBack;
import sk.sivak.eldritchhorror.core.constants.spell.SpellEffectImpl;

public class PoisonMistSpellBack4 extends AbstractSpellBack {

    public PoisonMistSpellBack4() {
        // Immediately resolve a Combat Encounter against each Monster on your space in the order of your choice.
        addSpellEffect("0", new SpellEffectImpl("The magic only gives away your location."));
        // Lose 1 Sanity.
        addSpellEffect("1-2", new SpellEffectImpl("The mists's effects are horrible to behold."));
        // You may choose Monsters for this card's effect as if each Monster's toughness is reduced by 1 to a minimum of 1.
        addSpellEffect("3+", new SpellEffectImpl("You conjure a great cloud of gas that blocks out the sun."));
    }
}
