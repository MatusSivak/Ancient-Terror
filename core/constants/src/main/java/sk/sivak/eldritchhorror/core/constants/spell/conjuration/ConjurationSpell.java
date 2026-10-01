package sk.sivak.eldritchhorror.core.constants.spell.conjuration;

import sk.sivak.eldritchhorror.core.constants.spell.AbstractSpellInfo;
import sk.sivak.eldritchhorror.core.constants.spell.SpellId;
import sk.sivak.eldritchhorror.core.constants.spell.SpellTrait;

public class ConjurationSpell extends AbstractSpellInfo {

    public ConjurationSpell() {
        traits.add(SpellTrait.RITUAL);
    }

    @Override
    public SpellId getId() {
        return SpellId.CONJURATION;
    }

    @Override
    public String getName() {
        return "Conjuration";
    }

    @Override
    public String getDescription() {
        return "ACTION: Test Lore+1.\n" +
                "Pass → gain 1 Item or Trinket\n" +
                "from the reserve with value\n" +
                "up to your result. Then flip this card.";
    }
}
