package sk.sivak.eldritchhorror.core.constants.spell.poisonmist;

import sk.sivak.eldritchhorror.core.constants.spell.AbstractSpellInfo;
import sk.sivak.eldritchhorror.core.constants.spell.SpellId;
import sk.sivak.eldritchhorror.core.constants.spell.SpellTrait;

public class PoisonMistSpell extends AbstractSpellInfo {

    public PoisonMistSpell() {
        traits.add(SpellTrait.RITUAL);
    }

    @Override
    public String getName() {
        return "Poison Mist";
    }

    @Override
    public String getDescription() {
        return "ACTION: Test Lore +1.\n" +
                "Pass → discard Monsters on your space\n" +
                "with total toughness up to your result.\n" +
                "Then flip this card.";
    }

    @Override
    public SpellId getId() {
        return SpellId.POISON_MIST;
    }

    @Override
    public boolean hasReckoning() {
        return false;
    }
}
