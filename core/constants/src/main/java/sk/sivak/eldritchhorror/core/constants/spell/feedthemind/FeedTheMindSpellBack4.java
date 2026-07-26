package sk.sivak.eldritchhorror.core.constants.spell.feedthemind;

import sk.sivak.eldritchhorror.core.constants.spell.AbstractSpellBack;
import sk.sivak.eldritchhorror.core.constants.spell.SpellEffectImpl;

public class FeedTheMindSpellBack4 extends AbstractSpellBack {

    public FeedTheMindSpellBack4() {
        // Discard all Improvement tokens unless you discard this card.
        addSpellEffect("0", new SpellEffectImpl("Your magic goes awry and wreaks havoc on your mind."));
        // The chosen investigator loses 1 Sanity.
        addSpellEffect("1-2", new SpellEffectImpl("Accessing one's memories requires unhinging the mind."));
        // The chosen investigator gains 1 Clue.
        addSpellEffect("3+", new SpellEffectImpl("Suddenly, everything you've learned has formed a greater picture in your mind's eye."));
    }
}
