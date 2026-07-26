package sk.sivak.eldritchhorror.core.model;

import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.spell.SpellId;
import sk.sivak.eldritchhorror.core.constants.spell.SpellInfo;
import sk.sivak.eldritchhorror.core.constants.spell.SpellTrait;
import sk.sivak.eldritchhorror.core.model.save.SaveDataWrite;
import sk.sivak.eldritchhorror.core.model.save.SpellsDeckSaveDataRead;

public interface SpellsDeckWrite extends SpellsDeckRead {

    void createDeck();

    SpellInfo gainSpell(SpellId spellId, InvestigatorId investigatorId);

    SpellInfo gainSpell(SpellTrait spellTrait, InvestigatorId investigatorId);

    SpellInfo gainSpell(InvestigatorId investigatorId);

    void discard(InvestigatorId investigatorId, SpellInfo spellInfo);

    void disable(SpellInfo spellInfo);

    void enable(SpellInfo spellInfo);

    void changeOwner(SpellInfo spellInfo, InvestigatorId newOwner);

    SaveDataWrite.SpellsDeckSaveDataWrite save();

    void load(SpellsDeckSaveDataRead saveData);
}
