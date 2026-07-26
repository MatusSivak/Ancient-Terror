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
        return "Test Lore+2. If you pass,\n" +
                "discard one Monster on the nearest Gate\n" +
                "with toughness <= your test result.\n\n" +
                "Then flip this card.";
    }
}
