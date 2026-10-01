package sk.sivak.eldritchhorror.core.constants.spell.clairvoyance;

import sk.sivak.eldritchhorror.core.constants.spell.AbstractSpellInfo;
import sk.sivak.eldritchhorror.core.constants.spell.SpellId;
import sk.sivak.eldritchhorror.core.constants.spell.SpellTrait;

public class ClairvoyanceSpell extends AbstractSpellInfo {

    public ClairvoyanceSpell() {
        traits.add(SpellTrait.INCANTATION);
    }

    @Override
    public SpellId getId() {
        return SpellId.CLAIRVOYANCE;
    }

    @Override
    public String getName() {
        return "Clairvoyance";
    }

    @Override
    public String getDescription() {
        return "Encounter Phase: you may test Lore.\n" +
                "Pass → encounter a Clue as if on its\n" +
                "space, ignoring Monsters there.\n" +
                "Then flip this card.";
    }
}
