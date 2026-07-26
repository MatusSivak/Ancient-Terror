package sk.sivak.eldritchhorror.core.constants.spell.shriveling;

import sk.sivak.eldritchhorror.core.constants.spell.AbstractSpellInfo;
import sk.sivak.eldritchhorror.core.constants.spell.SpellId;
import sk.sivak.eldritchhorror.core.constants.spell.SpellTrait;

public class ShrivelingSpell extends AbstractSpellInfo {
    public ShrivelingSpell() {
        traits.add(SpellTrait.RITUAL);
    }

    @Override
    public String getName() {
        return "Shriveling";
    }

    @Override
    public String getDescription() {
        return "ACTION: Test Lore. If you pass,\n" +
                "choose a Monster on your space\n" +
                "to lose 2 Health.\n" +
                "Then flip this card.";
    }

    @Override
    public SpellId getId() {
        return SpellId.SHRIVELING;
    }

    @Override
    public boolean hasReckoning() {
        return false;
    }
}
