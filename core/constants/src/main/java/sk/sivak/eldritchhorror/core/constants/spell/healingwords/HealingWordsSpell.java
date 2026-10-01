package sk.sivak.eldritchhorror.core.constants.spell.healingwords;

import sk.sivak.eldritchhorror.core.constants.spell.AbstractSpellInfo;
import sk.sivak.eldritchhorror.core.constants.spell.SpellId;
import sk.sivak.eldritchhorror.core.constants.spell.SpellTrait;

public class HealingWordsSpell extends AbstractSpellInfo {

    public HealingWordsSpell() {
        traits.add(SpellTrait.INCANTATION);
    }

    @Override
    public SpellId getId() {
        return SpellId.HEALING_WORDS;
    }

    @Override
    public String getName() {
        return "Healing Words";
    }

    @Override
    public String getDescription() {
        return "When an investigator on your space\n" +
                "Rests: you may test Lore.\n" +
                "Pass → they recover 1 extra Health\n" +
                "and 1 extra Sanity. Then flip this card.";
    }
}
