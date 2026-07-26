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
        return "When resolving a Combat Encounter,\n" +
                "you may test Lore. If you pass,\n" +
                "gain +3 Strength during that encounter.\n" +
                "Then flip this card.";
    }
}
