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
        return "During the Encounter Phase,\n" +
                "you may test Lore.\n" +
                "If you pass, you may\n" +
                "choose to encounter a Clue\n" +
                "as if you are on its space,\n" +
                "ignoring Monsters on that space.\n" +
                "Then flip this card.";
    }
}
