package sk.sivak.eldritchhorror.core.eventtype.data;

import sk.sivak.eldritchhorror.core.constants.asset.AssetInfo;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;

/**
 * @author msivak
 */
public class DiscardAssetFromInvestigatorData {

    private InvestigatorId investigatorId;
    private AssetInfo assetInfo;
    private boolean forced;

    public DiscardAssetFromInvestigatorData(InvestigatorId investigatorId, AssetInfo assetInfo, boolean forced) {
        this.investigatorId = investigatorId;
        this.assetInfo = assetInfo;
        this.forced = forced;
    }

    public InvestigatorId getInvestigatorId() {
        return investigatorId;
    }

    public void setInvestigatorId(InvestigatorId investigatorId) {
        this.investigatorId = investigatorId;
    }

    public AssetInfo getAssetInfo() {
        return assetInfo;
    }

    public void setAssetInfo(AssetInfo assetInfo) {
        this.assetInfo = assetInfo;
    }

    public boolean isForced() {
        return forced;
    }

    @Override
    public String toString() {
        return "DiscardAssetFromInvestigatorData{" +
                "investigatorId=" + investigatorId +
                ", assetInfo=" + assetInfo +
                '}';
    }
}
