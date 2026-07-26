package sk.sivak.eldritchhorror.core.constants.spell.fleshward;

import sk.sivak.eldritchhorror.core.constants.spell.AbstractSpellInfo;
import sk.sivak.eldritchhorror.core.constants.spell.SpellId;
import sk.sivak.eldritchhorror.core.constants.spell.SpellTrait;

public class FleshWardSpell extends AbstractSpellInfo {

    public FleshWardSpell() {
        traits.add(SpellTrait.INCANTATION);
    }

    @Override
    public SpellId getId() {
        return SpellId.FLESH_WARD;
    }

    @Override
    public String getName() {
        return "Flesh Ward";
    }

    @Override
    public String getDescription() {
        return "Once per round,\n" +
                "when an investigator would lose Health,\n" +
                "you may test Lore.\n" +
                "If you pass, prevent that investigator\n" +
                "from losing up to 2 Health.\n" +
                "Then flip this card.";
    }
}
