package sk.sivak.eldritchhorror.core.constants.spell.wither;

import sk.sivak.eldritchhorror.core.constants.spell.AbstractSpellInfo;
import sk.sivak.eldritchhorror.core.constants.spell.SpellId;
import sk.sivak.eldritchhorror.core.constants.spell.SpellTrait;

public class WitherSpell extends AbstractSpellInfo {

    public WitherSpell() {
        traits.add(SpellTrait.INCANTATION);
    }

    @Override
    public SpellId getId() {
        return SpellId.WITHER;
    }

    @Override
    public String getName() {
        return "Wither";
    }

    @Override
    public String getDescription() {
        return "Combat: you may test Lore.\n" +
                "Pass → +3 Strength for that combat.\n" +
                "Then flip this card.";
    }
}
