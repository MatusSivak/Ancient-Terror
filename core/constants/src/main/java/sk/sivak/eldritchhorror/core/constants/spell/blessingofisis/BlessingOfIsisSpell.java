package sk.sivak.eldritchhorror.core.constants.spell.blessingofisis;

import sk.sivak.eldritchhorror.core.constants.spell.AbstractSpellInfo;
import sk.sivak.eldritchhorror.core.constants.spell.SpellId;
import sk.sivak.eldritchhorror.core.constants.spell.SpellTrait;

public class BlessingOfIsisSpell extends AbstractSpellInfo {

    public BlessingOfIsisSpell() {
        traits.add(SpellTrait.RITUAL);
    }

    @Override
    public SpellId getId() {
        return SpellId.BLESSING_OF_ISIS;
    }

    @Override
    public String getName() {
        return "Blessing of Isis";
    }

    @Override
    public String getDescription() {
        return "ACTION: Test Lore-1. If you pass,\n" +
                "choose an investigator on your space\n" +
                "that does not have a Blessed Condition\n" +
                "to gain a Blessed Condition.\n" +
                "Then flip this card.";
    }
}
