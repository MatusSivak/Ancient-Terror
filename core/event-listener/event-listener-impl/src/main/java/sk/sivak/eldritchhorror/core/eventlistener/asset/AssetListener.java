package sk.sivak.eldritchhorror.core.eventlistener.asset;

import sk.sivak.eldritchhorror.core.constants.asset.AssetInfo;

/**
 * @author msivak
 */
public interface AssetListener<T extends AssetInfo> {

    T getAssetInfo();
}
