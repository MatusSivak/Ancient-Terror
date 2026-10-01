package sk.sivak.eldritchhorror.core.constants.spell.stormofspirits;

import sk.sivak.eldritchhorror.core.constants.spell.AbstractSpellInfo;
import sk.sivak.eldritchhorror.core.constants.spell.SpellId;
import sk.sivak.eldritchhorror.core.constants.spell.SpellTrait;

public class StormOfSpiritsSpell extends AbstractSpellInfo {

    public StormOfSpiritsSpell() {
        traits.add(SpellTrait.INCANTATION);
    }

    @Override
    public SpellId getId() {
        return SpellId.STORM_OF_SPIRITS;
    }

    @Override
    public String getName() {
        return "Storm of Spirits";
    }

    @Override
    public String getDescription() {
        return "Combat: you may test Lore instead\n" +
                "of Strength (same modifier).\n" +
                "Then flip this card.";
    }
}
