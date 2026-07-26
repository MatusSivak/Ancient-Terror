package sk.sivak.eldritchhorror.core.constants.displayasset;

import sk.sivak.eldritchhorror.core.constants.artifact.ArtifactInfo;
import sk.sivak.eldritchhorror.core.constants.asset.AssetInfo;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionInfo;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.spell.SpellInfo;

public class ShowCardRequest {
    private AssetInfo assetInfo;
    private ArtifactInfo artifactInfo;
    private ConditionInfo conditionInfo;
    private SpellInfo spellInfo;
    private String title;
    private DisplayAssetResponseType displayAssetResponseType = DisplayAssetResponseType.OK;
    private HideType hideType = HideType.RETURN;
    private AssetOriginType assetOriginType = AssetOriginType.INVENTORY;
    private String portraitTextureName;
    private boolean hiddenTemplate;

    public ShowCardRequest() {
    }

    public String getPortraitTextureName() {
        return portraitTextureName;
    }

    public void setPortraitBeforeTitle(InvestigatorId investigatorId) {
        this.portraitTextureName = "investigator/" + investigatorId.name() + ".png";;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public DisplayAssetResponseType getDisplayAssetResponseType() {
        return displayAssetResponseType;
    }

    public void setDisplayAssetResponseType(DisplayAssetResponseType displayAssetResponseType) {
        this.displayAssetResponseType = displayAssetResponseType;
    }

    public AssetInfo getAssetInfo() {
        return assetInfo;
    }

    public void setAssetInfo(AssetInfo assetInfo) {
        this.assetInfo = assetInfo;
    }

    public ArtifactInfo getArtifactInfo() {
        return artifactInfo;
    }

    public void setArtifactInfo(ArtifactInfo artifactInfo) {
        this.artifactInfo = artifactInfo;
    }

    public AssetOriginType getAssetOriginType() {
        return assetOriginType;
    }

    public void setAssetOriginType(AssetOriginType assetOriginType) {
        this.assetOriginType = assetOriginType;
    }

    public ConditionInfo getConditionInfo() {
        return conditionInfo;
    }

    public void setConditionInfo(ConditionInfo conditionInfo) {
        this.conditionInfo = conditionInfo;
    }

    public SpellInfo getSpellInfo() {
        return spellInfo;
    }

    public void setSpellInfo(SpellInfo spellInfo) {
        this.spellInfo = spellInfo;
    }

    public HideType getHideType() {
        return hideType;
    }

    public void setHideType(HideType hideType) {
        this.hideType = hideType;
    }

    public void setHiddenTemplate(boolean hiddenTemplate) {
        this.hiddenTemplate = hiddenTemplate;
    }

    public boolean isHiddenTemplate() {
        return hiddenTemplate;
    }
}
