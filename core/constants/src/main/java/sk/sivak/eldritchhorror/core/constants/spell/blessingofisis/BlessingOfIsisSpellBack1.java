package sk.sivak.eldritchhorror.core.constants.spell.blessingofisis;

import sk.sivak.eldritchhorror.core.constants.spell.AbstractSpellBack;
import sk.sivak.eldritchhorror.core.constants.spell.SpellEffectImpl;

public class BlessingOfIsisSpellBack1 extends AbstractSpellBack {

    public BlessingOfIsisSpellBack1() {
        // The chosen investigator gains 1 Cursed Condition.
        addSpellEffect("0", new SpellEffectImpl("The goddess has been angered, and her wrath is terrible to behold."));
        // Lose 1 Sanity.
        addSpellEffect("1", new SpellEffectImpl("You feel an almighty power surge through you and it is too much for your mind to handle."));
        // No additional effects.
        addSpellEffect("2+", new SpellEffectImpl("The goddess smiles upon your cause."));
    }
}
