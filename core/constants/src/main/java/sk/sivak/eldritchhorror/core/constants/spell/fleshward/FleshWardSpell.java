package sk.sivak.eldritchhorror.core.constants.spell.fleshward;

import sk.sivak.eldritchhorror.core.constants.spell.AbstractSpellInfo;
import sk.sivak.eldritchhorror.core.constants.spell.SpellId;
import sk.sivak.eldritchhorror.core.constants.spell.SpellTrait;

public class FleshWardSpell extends AbstractSpellInfo {

    public FleshWardSpell() {
        traits.add(SpellTrait.INCANTATION);
    }

    @Override
    public SpellId getId() {
        return SpellId.FLESH_WARD;
    }

    @Override
    public String getName() {
        return "Flesh Ward";
    }

    @Override
    public String getDescription() {
        return "Once per round, when an investigator\n" +
                "would lose Health: you may test Lore.\n" +
                "Pass → prevent up to 2 Health loss.\n" +
                "Then flip this card.";
    }
}
