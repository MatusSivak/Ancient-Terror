package sk.sivak.eldritchhorror.core.model;

import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.spell.SpellId;
import sk.sivak.eldritchhorror.core.constants.spell.SpellInfo;
import sk.sivak.eldritchhorror.core.constants.spell.SpellTrait;

import java.util.List;

public interface SpellsDeckRead {
    boolean hasSpell(InvestigatorId investigatorId, SpellId spellId);

    boolean hasTrait(InvestigatorId investigatorId, SpellTrait trait);

    List<SpellInfo> getSpells(InvestigatorId investigatorId);

    boolean canGetTrait(InvestigatorId investigatorId, SpellTrait trait);

    boolean canGetRandomSpell(InvestigatorId activeInvestigatorId);

    InvestigatorId getSpellOwner(SpellInfo spellInfo);
}
