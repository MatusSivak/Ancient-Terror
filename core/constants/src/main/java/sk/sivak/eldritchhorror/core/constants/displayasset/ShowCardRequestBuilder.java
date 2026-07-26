package sk.sivak.eldritchhorror.core.constants.displayasset;

import sk.sivak.eldritchhorror.core.constants.artifact.ArtifactInfo;
import sk.sivak.eldritchhorror.core.constants.asset.AssetInfo;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionInfo;

public class ShowCardRequestBuilder {

    private ShowCardRequest showCardRequest;

    public ShowCardRequestBuilder(AssetInfo assetInfo) {
        showCardRequest = new ShowCardRequest();
        showCardRequest.setAssetInfo(assetInfo);
    }

    public ShowCardRequestBuilder(ArtifactInfo artifactInfo) {
        showCardRequest = new ShowCardRequest();
        showCardRequest.setArtifactInfo(artifactInfo);
    }

    public ShowCardRequestBuilder(ConditionInfo conditionInfo) {
        showCardRequest = new ShowCardRequest();
        showCardRequest.setConditionInfo(conditionInfo);
    }

    public ShowCardRequestBuilder withTitle(String title) {
        showCardRequest.setTitle(title);
        return this;
    }

    public ShowCardRequestBuilder withDisplayAssetResponseType(DisplayAssetResponseType displayAssetResponseType) {
        showCardRequest.setDisplayAssetResponseType(displayAssetResponseType);
        return this;
    }

    public ShowCardRequestBuilder withAssetHideType(HideType hideType) {
        showCardRequest.setHideType(hideType);
        return this;
    }

    public ShowCardRequestBuilder withAssetOriginType(AssetOriginType assetOriginType) {
        showCardRequest.setAssetOriginType(assetOriginType);
        return this;
    }

    public ShowCardRequest build() {
        return showCardRequest;
    }
}
