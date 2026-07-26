package sk.sivak.eldritchhorror.core.eventlistener.provider;

import sk.sivak.eldritchhorror.core.constants.asset.AssetId;
import sk.sivak.eldritchhorror.core.constants.asset.AssetInfo;
import sk.sivak.eldritchhorror.core.eventlistener.asset.AbstractAssetListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AssetListenerProviderImpl implements AssetListenerProvider {
    private Map<AssetId, AbstractAssetListener<? extends AssetInfo>> listenersMap;

    @Override
    public void init() {
        listenersMap = new HashMap<>();
    }

    @Override
    public AbstractAssetListener[] getAssetListeners() {
        ArrayList<AbstractAssetListener> allListeners = new ArrayList<>();
        for (AssetId assetId : AssetId.values()) {
            allListeners.add(getAssetListener(assetId));
        }
        return allListeners.toArray(new AbstractAssetListener[allListeners.size()]);
    }

    @Override
    public AbstractAssetListener<? extends AssetInfo> getAssetListener(AssetId assetId) {
        AbstractAssetListener<?> listener = listenersMap.get(assetId);
        if (listener == null) {
            try {
                listener = (AbstractAssetListener<?>) Class.forName(assetId.getAssetListenerClassName()).newInstance();
                listenersMap.put(assetId, listener);
            } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
                throw new IllegalArgumentException(e);
            }
        }
        return listener;
    }
}
