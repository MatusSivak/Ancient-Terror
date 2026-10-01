package sk.sivak.eldritchhorror.core.constants.spell.banishment;

import sk.sivak.eldritchhorror.core.constants.spell.AbstractSpellInfo;
import sk.sivak.eldritchhorror.core.constants.spell.SpellId;
import sk.sivak.eldritchhorror.core.constants.spell.SpellTrait;

public class BanishmentSpell extends AbstractSpellInfo {

    public BanishmentSpell() {
        traits.add(SpellTrait.RITUAL);
    }

    @Override
    public SpellId getId() {
        return SpellId.BANISHMENT;
    }

    @Override
    public String getName() {
        return "Banishment";
    }

    @Override
    public String getDescription() {
        return "Test Lore+2.\n" +
                "Pass → discard 1 Monster on the\n" +
                "nearest Gate with toughness up to\n" +
                "your result. Then flip this card.";
    }
}
