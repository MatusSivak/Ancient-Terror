package sk.sivak.eldritchhorror.core.eventtype.data;

import sk.sivak.eldritchhorror.core.constants.asset.AssetInfo;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;

import java.util.LinkedList;
import java.util.List;

/**
 * @author msivak
 */
public class TestAssets {

    private Stat stat;
    private List<AssetInfo> testAssetsList = new LinkedList<>();

    public TestAssets(Stat stat) {
        this.stat = stat;
    }

    public Stat getStat() {
        return stat;
    }

    public void setStat(Stat stat) {
        this.stat = stat;
    }

    public List<AssetInfo> getTestAssetsList() {
        return testAssetsList;
    }

    public void setTestAssetsList(List<AssetInfo> testAssetsList) {
        this.testAssetsList = testAssetsList;
    }
}
