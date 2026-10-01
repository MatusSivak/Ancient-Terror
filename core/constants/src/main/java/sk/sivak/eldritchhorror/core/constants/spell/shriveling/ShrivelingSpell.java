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
        return "ACTION: Test Lore.\n" +
                "Pass → 1 Monster on your space\n" +
                "loses 2 Health. Then flip this card.";
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
