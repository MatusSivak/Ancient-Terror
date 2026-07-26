package sk.sivak.eldritchhorror.core.constants.spell.feedthemind;

import sk.sivak.eldritchhorror.core.constants.spell.AbstractSpellBack;
import sk.sivak.eldritchhorror.core.constants.spell.SpellEffectImpl;

public class FeedTheMindSpellBack2 extends AbstractSpellBack {

    public FeedTheMindSpellBack2() {
        //  If you did not roll any 4's, discard this card.
        addSpellEffect("0", new SpellEffectImpl("You were unable to sufficiently focus your mind. " +
                "You wonder if you simply lack the mental discipline this spell requires."));
        // Lose 1 Sanity.
        addSpellEffect("1+", new SpellEffectImpl("Casting your thought into the ether, " +
                "your mind returns with strange knowledge. " +
                "You may not entirely be the same person you were before."));
    }
}
