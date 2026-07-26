package sk.sivak.eldritchhorror.core.constants.spell.banishment;

import sk.sivak.eldritchhorror.core.constants.spell.AbstractSpellBack;
import sk.sivak.eldritchhorror.core.constants.spell.SpellEffectImpl;

public class BanishmentSpellBack2 extends AbstractSpellBack {

    public BanishmentSpellBack2() {
        // Lose 2 Sanity unless you discard this card.
        addSpellEffect("0-2", new SpellEffectImpl("Grasping the magic proves too difficult for your mind to handle."));
        // Discard 1 additional Monster on that space with toughness equal to or less than your test result.
        addSpellEffect("3+", new SpellEffectImpl("The arcane words take effect, and evil is banished to the world from which it came."));
    }
}
