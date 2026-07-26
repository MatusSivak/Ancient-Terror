package sk.sivak.eldritchhorror.core.action.impl.saveload;

import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.constants.asset.AssetInfo;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.model.save.SaveDataRead;

public class LoadAssetsDeckHelper {

    public static void loadAssetsDeck(SaveDataRead saveData) {
        ServicePlatform.get().getAssetsDeck().load(saveData.getAssetsDeck());
        if (saveData.getModel().getInvestigatorCount() == 1) {
            ServicePlatform.get().getAssetsDeck().removeTeamworkAssets();
        }
        for (String investigatorIdAsString : saveData.getAssetsDeck().getInvestigatorAssetsMap().keySet()) {
            InvestigatorId investigatorId = InvestigatorId.valueOf(investigatorIdAsString);
            for (AssetInfo assetInfo : ServicePlatform.get().getAssetsDeck().getAssets(investigatorId)) {
                ServicePlatform.get().getAssetListenerProvider().getAssetListener(assetInfo.getId()).justLoadWithOwner(investigatorId, assetInfo);
                if (assetInfo.isDisabled()) {
                    ServicePlatform.get().getAssetListenerProvider().getAssetListener(assetInfo.getId()).disable();
                }
            }
        }
    }
}
