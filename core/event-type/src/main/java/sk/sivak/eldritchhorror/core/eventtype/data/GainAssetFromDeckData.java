package sk.sivak.eldritchhorror.core.eventtype.data;

import sk.sivak.eldritchhorror.core.constants.asset.AssetId;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;

/**
 * @author msivak
 */
public class GainAssetFromDeckData {

    private InvestigatorId investigatorId;
    private AssetId assetId;

    public GainAssetFromDeckData(InvestigatorId investigatorId, AssetId assetId) {
        this.investigatorId = investigatorId;
        this.assetId = assetId;
    }

    public InvestigatorId getInvestigatorId() {
        return investigatorId;
    }

    public void setInvestigatorId(InvestigatorId investigatorId) {
        this.investigatorId = investigatorId;
    }

    public AssetId getAssetId() {
        return assetId;
    }

    public void setAssetId(AssetId assetId) {
        this.assetId = assetId;
    }

    @Override
    public String toString() {
        return "GainAssetData{" +
                "investigatorId=" + investigatorId +
                ", assetId=" + assetId +
                '}';
    }
}
