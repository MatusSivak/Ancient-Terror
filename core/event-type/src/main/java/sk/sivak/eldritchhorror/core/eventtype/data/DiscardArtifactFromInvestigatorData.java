package sk.sivak.eldritchhorror.core.eventtype.data;

import sk.sivak.eldritchhorror.core.constants.artifact.ArtifactInfo;
import sk.sivak.eldritchhorror.core.constants.asset.AssetInfo;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;

/**
 * @author msivak
 */
public class DiscardArtifactFromInvestigatorData {

    private InvestigatorId investigatorId;
    private ArtifactInfo artifactInfo;
    private boolean forced;

    public DiscardArtifactFromInvestigatorData(InvestigatorId investigatorId, ArtifactInfo artifactInfo, boolean forced) {
        this.investigatorId = investigatorId;
        this.artifactInfo = artifactInfo;
        this.forced = forced;
    }

    public InvestigatorId getInvestigatorId() {
        return investigatorId;
    }

    public void setInvestigatorId(InvestigatorId investigatorId) {
        this.investigatorId = investigatorId;
    }

    public ArtifactInfo getArtifactInfo() {
        return artifactInfo;
    }

    public void setArtifactInfo(ArtifactInfo artifactInfo) {
        this.artifactInfo = artifactInfo;
    }

    public boolean isForced() {
        return forced;
    }

    @Override
    public String toString() {
        return "DiscardAssetFromInvestigatorData{" +
                "investigatorId=" + investigatorId +
                ", artifactInfo=" + artifactInfo +
                '}';
    }
}
