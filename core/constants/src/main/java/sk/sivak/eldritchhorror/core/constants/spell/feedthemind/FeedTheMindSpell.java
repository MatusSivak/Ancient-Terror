package sk.sivak.eldritchhorror.core.constants.spell.feedthemind;

import sk.sivak.eldritchhorror.core.constants.spell.AbstractSpellInfo;
import sk.sivak.eldritchhorror.core.constants.spell.SpellId;
import sk.sivak.eldritchhorror.core.constants.spell.SpellTrait;

public class FeedTheMindSpell extends AbstractSpellInfo {

    public FeedTheMindSpell() {
        traits.add(SpellTrait.RITUAL);
    }

    @Override
    public SpellId getId() {
        return SpellId.FEED_THE_MIND;
    }

    @Override
    public String getName() {
        return "Feed the Mind";
    }

    @Override
    public String getDescription() {
        return "ACTION: Test Lore-1.\n" +
                "Pass → an investigator on your space\n" +
                "improves 1 skill of their choice.\n" +
                "Then flip this card.";
    }
}
