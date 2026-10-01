package sk.sivak.eldritchhorror.core.constants.spell.instillbravery;

import sk.sivak.eldritchhorror.core.constants.spell.AbstractSpellInfo;
import sk.sivak.eldritchhorror.core.constants.spell.SpellId;
import sk.sivak.eldritchhorror.core.constants.spell.SpellTrait;

public class InstillBraverySpell extends AbstractSpellInfo {

    public InstillBraverySpell() {
        traits.add(SpellTrait.INCANTATION);
    }

    @Override
    public SpellId getId() {
        return SpellId.INSTILL_BRAVERY;
    }

    @Override
    public String getName() {
        return "Instill Bravery";
    }

    @Override
    public String getDescription() {
        return "Once per round, when an investigator\n" +
                "would lose Sanity: you may test Lore.\n" +
                "Pass → prevent up to 2 Sanity loss.\n" +
                "Then flip this card.";
    }
}
