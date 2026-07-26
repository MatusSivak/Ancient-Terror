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
        return "Once per round,\n" +
                "when an investigator would lose Sanity,\n" +
                "you may test Lore.\n" +
                "If you pass, prevent that investigator\n" +
                "from losing up to 2 Sanity.\n" +
                "Then flip this card.";
    }
}
