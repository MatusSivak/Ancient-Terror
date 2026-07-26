package sk.sivak.eldritchhorror.core.action.impl.saveload;

import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.constants.artifact.ArtifactInfo;
import sk.sivak.eldritchhorror.core.constants.asset.AssetInfo;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.spell.SpellInfo;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.model.InvestigatorRead;
import sk.sivak.eldritchhorror.core.model.save.InvestigatorsSaveDataRead;
import sk.sivak.eldritchhorror.core.model.save.SaveDataRead;

public class LoadInvestigatorsHelper {
    public static void loadInvestigators(SaveDataRead saveData) {
        ServicePlatform.get().getInvestigators().load(saveData.getInvestigators(), ServicePlatform.get().getInitGameController().hasPurchasedBonusInvestigators());
        for (InvestigatorsSaveDataRead.InvestigatorSaveDataRead aliveInvestigator : saveData.getInvestigators().getSelectedInvestigators()) {
            InvestigatorId investigatorId = aliveInvestigator.getInvestigatorId();
            ServicePlatform.get().getEventListenerProvider().justRegisterInvestigatorListeners(investigatorId);
            ServicePlatform.get().getEventQueue().addBeforeEventListener(
                    ServicePlatform.get().getActionListenerProvider().getInvestigatorActionListener(investigatorId), BeforeAfterEvent.FIND_ACTIONS);
        }
        ServicePlatform.get().getPerformedActions().clearPerformedActionsMap();
        ServicePlatform.get().getPerformedActions().init(saveData.getInvestigators().getInvestigatorOrder().toArray(new InvestigatorId[0]));
        ServicePlatform.get().getInvestigators().initActiveInvestigator();

        for (InvestigatorsSaveDataRead.DefeatedInvestigatorSaveDataRead defeatedInvestigator : saveData.getInvestigators().getDefeatedInvestigators()) {
            // unregister assets
            for (AssetInfo asset : ServicePlatform.get().getAssetsDeck().getAssets(defeatedInvestigator.getInvestigatorId())) {
                ServicePlatform.get().getAssetListenerProvider().getAssetListener(asset.getId()).unregister();
            }

            // unregister artifacts
            for (ArtifactInfo artifact : ServicePlatform.get().getArtifactsDeck().getArtifacts(defeatedInvestigator.getInvestigatorId())) {
                ServicePlatform.get().getArtifactListenerProvider().getArtifactListener(artifact.getId()).unregister();
            }

            // unregister spells
            for (SpellInfo spellInfo : ServicePlatform.get().getSpellsDeck().getSpells(defeatedInvestigator.getInvestigatorId())) {
                ServicePlatform.get().getSpellListenerProvider().getSpellListener(spellInfo).unregister();
            }


            ServicePlatform.get().getEventListenerProvider().registerDefeatedListener(defeatedInvestigator.getInvestigatorId(), defeatedInvestigator.getDefeatedByHealth());

        }
    }
}
