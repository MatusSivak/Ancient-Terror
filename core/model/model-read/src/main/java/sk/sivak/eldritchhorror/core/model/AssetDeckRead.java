package sk.sivak.eldritchhorror.core.model;

import sk.sivak.eldritchhorror.core.constants.asset.AssetId;
import sk.sivak.eldritchhorror.core.constants.asset.AssetInfo;
import sk.sivak.eldritchhorror.core.constants.asset.AssetTrait;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;

import java.util.List;

/**
 * @author msivak
 */
public interface AssetDeckRead {
    List<AssetInfo> getReserve();

    List<AssetId> getLockedAssets();

    List<AssetInfo> getDiscardPile();

    List<AssetInfo> getAssets(InvestigatorId investigatorId);

    boolean hasAsset(InvestigatorId investigatorId, AssetId assetId);

    boolean hasAsset(InvestigatorId investigatorId, AssetTrait assetTrait);

    AssetInfo findFirst(AssetTrait trait);

    AssetInfo findFirst();

    AssetInfo find(AssetId assetId);

    boolean isAvailable(AssetTrait trait);

}
