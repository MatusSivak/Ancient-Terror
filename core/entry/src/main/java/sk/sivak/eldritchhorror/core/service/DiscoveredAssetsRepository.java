package sk.sivak.eldritchhorror.core.service;

import sk.sivak.eldritchhorror.core.constants.asset.AssetId;

import java.util.List;

public class DiscoveredAssetsRepository implements DiscoveredRepository<AssetId>{

    private final DiscoveredRepositoryTemplate<AssetId, AssetIdHasManyValues> template;

    private static class AssetIdHasManyValues implements HasManyValues<AssetId> {

        @Override
        public AssetId[] values() {
            return AssetId.values();
        }
    }

    public DiscoveredAssetsRepository() {
        template = new DiscoveredRepositoryTemplate<>(new AssetIdHasManyValues());
        template.setKeyName("discoveredAssets");
    }

    @Override
    public void saveDiscovered(List<AssetId> discoveredAssets) {
        template.saveData(discoveredAssets);
    }

    @Override
    public List<AssetId> getDiscovered() {
        return template.getDiscovered();
    }
}
