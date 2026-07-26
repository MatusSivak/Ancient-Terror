package sk.sivak.eldritchhorror.core.constants.spell.feedthemind;

import sk.sivak.eldritchhorror.core.constants.spell.AbstractSpellBack;
import sk.sivak.eldritchhorror.core.constants.spell.SpellEffectImpl;

public class FeedTheMindSpellBack3 extends AbstractSpellBack {

    public FeedTheMindSpellBack3() {
        // Discard this card unless you gain an Amnesia Condition.
        addSpellEffect("0", new SpellEffectImpl("The gateway to the mind is a two-way portal, " +
                "and your memories begin to scatter."));
        // Lose 1 Sanity.
        addSpellEffect("1", new SpellEffectImpl("A wealth of knowledge overwhelms your mind."));
        // No additional effect.
        addSpellEffect("2+", new SpellEffectImpl("Your previous training suddenly becomes clear to you."));
    }
}
