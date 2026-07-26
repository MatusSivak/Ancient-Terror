package sk.sivak.eldritchhorror.core.constants.spell.arcaneinsight;

import sk.sivak.eldritchhorror.core.constants.spell.AbstractSpellInfo;
import sk.sivak.eldritchhorror.core.constants.spell.SpellId;
import sk.sivak.eldritchhorror.core.constants.spell.SpellTrait;

public class ArcaneInsightSpell extends AbstractSpellInfo {

    public ArcaneInsightSpell() {
        traits.add(SpellTrait.RITUAL);
    }

    @Override
    public SpellId getId() {
        return SpellId.ARCANE_INSIGHT;
    }

    @Override
    public String getName() {
        return "Arcane Insight";
    }

    @Override
    public String getDescription() {
        return "Choose yourself or another investigator\n" +
                "on any space and test Lore-2.\n" +
                "Roll one additional die for\n" +
                "each Tome possession you have.\n" +
                "If you pass, that investigator gains one Clue.\n" +
                "Then flip this card.";
    }
}
