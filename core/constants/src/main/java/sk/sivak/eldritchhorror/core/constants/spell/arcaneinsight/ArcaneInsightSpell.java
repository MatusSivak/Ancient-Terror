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
        return "Test Lore-2, +1 die per Tome you have.\n" +
                "Pass → any investigator gains 1 Clue.\n" +
                "Then flip this card.";
    }
}
