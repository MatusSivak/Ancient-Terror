package sk.sivak.eldritchhorror.core.model.save;

import sk.sivak.eldritchhorror.core.constants.asset.AssetId;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;

import java.util.List;
import java.util.Map;

public interface AssetsDeckSaveDataRead {
    List<AssetId> getDiscarded();

    List<AssetId> getReserve();

    Map<String, List<? extends OwnedAssetSaveDataRead>> getInvestigatorAssetsMap();

    interface OwnedAssetSaveDataRead {
        AssetId getAssetId();

        boolean isDisabled();
    }
}
