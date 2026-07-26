package sk.sivak.eldritchhorror.core.constants.reserve;

import sk.sivak.eldritchhorror.core.constants.asset.AssetInfo;

import java.util.LinkedList;
import java.util.List;

public class ShowReserveDragAndDropOutput {

    private List<AssetInfo> selectedAssets = new LinkedList<>();

    public List<AssetInfo> getSelectedAssets() {
        return selectedAssets;
    }

    public void setSelectedAssets(List<AssetInfo> selectedAssets) {
        this.selectedAssets = selectedAssets;
    }
}
