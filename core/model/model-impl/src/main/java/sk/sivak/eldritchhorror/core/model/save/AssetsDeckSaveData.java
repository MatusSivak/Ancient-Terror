package sk.sivak.eldritchhorror.core.model.save;

import sk.sivak.eldritchhorror.core.constants.asset.AssetId;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class AssetsDeckSaveData implements SaveDataWrite.AssetsDeckSaveDataWrite {
    private List<AssetId> discarded = new LinkedList<>();
    private List<AssetId> reserve = new LinkedList<>();
    private Map<String, List<? extends OwnedAssetSaveDataRead>> investigatorAssetsMap = new HashMap<>();

    public void setDiscarded(List<AssetId> discarded) {
        this.discarded = discarded;
    }

    public void setReserve(List<AssetId> reserve) {
        this.reserve = reserve;
    }

    public void setInvestigatorAssetsMap(Map<String, List<? extends OwnedAssetSaveDataRead>> investigatorAssetsMap) {
        this.investigatorAssetsMap = investigatorAssetsMap;
    }

    @Override
    public List<AssetId> getDiscarded() {
        return discarded;
    }

    @Override
    public List<AssetId> getReserve() {
        return reserve;
    }

    @Override
    public Map<String, List<? extends OwnedAssetSaveDataRead>> getInvestigatorAssetsMap() {
        return investigatorAssetsMap;
    }

    public static class OwnedAssetSaveData implements OwnedAssetSaveDataWrite {
        private AssetId assetId;
        private boolean disabled;

        @Override
        public AssetId getAssetId() {
            return assetId;
        }

        public void setAssetId(AssetId assetId) {
            this.assetId = assetId;
        }

        @Override
        public boolean isDisabled() {
            return disabled;
        }

        public void setDisabled(boolean disabled) {
            this.disabled = disabled;
        }
    }
}