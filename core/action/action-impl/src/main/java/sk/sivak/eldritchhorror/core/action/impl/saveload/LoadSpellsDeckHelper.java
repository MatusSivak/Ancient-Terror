package sk.sivak.eldritchhorror.core.action.impl.saveload;

import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.constants.spell.SpellInfo;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.model.save.SaveDataRead;

public class LoadSpellsDeckHelper {
    public static void loadSpellsDeck(SaveDataRead saveData) {

        ServicePlatform.get().getSpellsDeck().load(saveData.getSpellsDeck());
        for (String investigatorIdAsString : saveData.getSpellsDeck().getInvestigatorSpellInfoMap().keySet()) {
            InvestigatorId investigatorId = InvestigatorId.valueOf(investigatorIdAsString);
            for (SpellInfo spellInfo : ServicePlatform.get().getSpellsDeck().getSpells(investigatorId)) {
                ServicePlatform.get().getSpellListenerProvider().getSpellListener(spellInfo).register(spellInfo, investigatorId);
                if (spellInfo.isDisabled()) {
                    ServicePlatform.get().getSpellListenerProvider().getSpellListener(spellInfo).disable();
                }
            }
        }
    }
}
