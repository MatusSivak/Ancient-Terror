package sk.sivak.eldritchhorror.core.constants.spell.plumbthevoid;

import sk.sivak.eldritchhorror.core.constants.spell.AbstractSpellInfo;
import sk.sivak.eldritchhorror.core.constants.spell.SpellId;
import sk.sivak.eldritchhorror.core.constants.spell.SpellTrait;

public class PlumbTheVoidSpell extends AbstractSpellInfo {

    public PlumbTheVoidSpell() {
        traits.add(SpellTrait.RITUAL);
    }

    @Override
    public SpellId getId() {
        return SpellId.PLUMB_THE_VOID;
    }

    @Override
    public String getName() {
        return "Plumb the Void";
    }

    @Override
    public String getDescription() {
        return "ACTION: Test Lore-1. If you pass,\n" +
                "an investigator of your choice\n" +
                "may move to any space.\n" +
                "Then flip this card.";
    }
}
