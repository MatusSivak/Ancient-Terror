package sk.sivak.eldritchhorror.core.constants.spell.mistsofreleh;

import sk.sivak.eldritchhorror.core.constants.spell.AbstractSpellInfo;
import sk.sivak.eldritchhorror.core.constants.spell.SpellId;
import sk.sivak.eldritchhorror.core.constants.spell.SpellTrait;

public class MistsOfRelehSpell extends AbstractSpellInfo {

    public MistsOfRelehSpell() {
        traits.add(SpellTrait.INCANTATION);
    }

    @Override
    public SpellId getId() {
        return SpellId.MISTS_OF_RELEH;
    }

    @Override
    public String getName() {
        return "Mists of Releh";
    }

    @Override
    public String getDescription() {
        return "Encounter Phase: you may test Lore.\n" +
                "Pass → choose an encounter as if\n" +
                "no Monsters are on your space.\n" +
                "Then flip this card.";
    }
}
