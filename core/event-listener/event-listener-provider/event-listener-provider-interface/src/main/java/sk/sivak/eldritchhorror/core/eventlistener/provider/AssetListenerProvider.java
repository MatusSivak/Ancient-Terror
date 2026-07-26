package sk.sivak.eldritchhorror.core.eventlistener.provider;

import sk.sivak.eldritchhorror.core.constants.asset.AssetId;
import sk.sivak.eldritchhorror.core.constants.asset.AssetInfo;
import sk.sivak.eldritchhorror.core.eventlistener.asset.AbstractAssetListener;

/**
 * @author msivak
 */
public interface AssetListenerProvider {

    AbstractAssetListener[] getAssetListeners();

    AbstractAssetListener<? extends AssetInfo> getAssetListener(AssetId assetId);

    void init();
}
