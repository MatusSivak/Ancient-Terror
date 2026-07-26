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
        return "During the Encounter Phase,\n" +
                "you may test Lore.\n" +
                "If you pass, you may\n" +
                "choose an encounter as if\n" +
                "there are no Monsters on your space.\n" +
                "Then flip this card.";
    }
}
