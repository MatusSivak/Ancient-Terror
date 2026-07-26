package sk.sivak.eldritchhorror.core.constants.spell.banishment;

import sk.sivak.eldritchhorror.core.constants.spell.AbstractSpellBack;
import sk.sivak.eldritchhorror.core.constants.spell.SpellEffectImpl;

public class BanishmentSpellBack4 extends AbstractSpellBack {

    public BanishmentSpellBack4() {
        // Move 1 Monster on any space to your space and immediately encounter it.
        addSpellEffect("0", new SpellEffectImpl("You misspeak the words and summon the beast to you."));
        // Lose 1 Sanity.
        addSpellEffect("1-2", new SpellEffectImpl("You glimpse through the portal, and the world you see shakes your nerves."));
        // Discard 1 Monster of your choice on that space regardless of its toughness instead.
        addSpellEffect("3+", new SpellEffectImpl("The magic is powerful."));
    }
}
