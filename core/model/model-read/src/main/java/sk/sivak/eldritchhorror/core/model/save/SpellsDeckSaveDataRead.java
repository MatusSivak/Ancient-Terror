package sk.sivak.eldritchhorror.core.model.save;

import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.spell.SpellId;

import java.util.List;
import java.util.Map;

public interface SpellsDeckSaveDataRead {

    Map<String, List<? extends SpellInfoSaveDataRead>> getInvestigatorSpellInfoMap();

    interface SpellInfoSaveDataRead {
        SpellId getSpellId();

        int getSpellBackId();

        boolean isDisabled();
    }
}
