package sk.sivak.eldritchhorror.core.constants.spell.intervene;

import sk.sivak.eldritchhorror.core.constants.spell.AbstractSpellInfo;
import sk.sivak.eldritchhorror.core.constants.spell.SpellId;
import sk.sivak.eldritchhorror.core.constants.spell.SpellTrait;

public class InterveneSpell extends AbstractSpellInfo {

    public InterveneSpell() {
        traits.add(SpellTrait.INCANTATION);
        traits.add(SpellTrait.TEAMWORK);
    }

    @Override
    public SpellId getId() {
        return SpellId.INTERVENE;
    }

    @Override
    public String getName() {
        return "Intervene";
    }

    @Override
    public String getDescription() {
        return "Another investigator's Combat:\n" +
                "you may test Lore.\n" +
                "Pass → they gain +3 Strength.\n" +
                "Then flip this card.";
    }
}
