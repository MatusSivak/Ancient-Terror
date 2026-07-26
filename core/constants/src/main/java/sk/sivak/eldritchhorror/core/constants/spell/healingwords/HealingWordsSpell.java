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
                "performs a Rest action,\n" +
                "you may test Lore. If you pass,\n" +
                "that investigator recovers\n" +
                "1 additional Health and\n" +
                "1 additional Sanity.\n" +
                "Then flip this card.";
    }
}
