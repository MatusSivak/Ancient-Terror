package sk.sivak.eldritchhorror.core.eventtype.data;

import sk.sivak.eldritchhorror.core.constants.artifact.ArtifactId;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;

/**
 * @author msivak
 */
public class GainArtifactFromDeckData {

    private InvestigatorId investigatorId;
    private ArtifactId artifactId;

    public GainArtifactFromDeckData(InvestigatorId investigatorId, ArtifactId artifactId) {
        this.investigatorId = investigatorId;
        this.artifactId = artifactId;
    }

    public InvestigatorId getInvestigatorId() {
        return investigatorId;
    }

    public void setInvestigatorId(InvestigatorId investigatorId) {
        this.investigatorId = investigatorId;
    }

    public ArtifactId getArtifactId() {
        return artifactId;
    }

    public void setArtifactId(ArtifactId artifactId) {
        this.artifactId = artifactId;
    }

    @Override
    public String toString() {
        return "GainArtifactFromDeckData{" +
                "investigatorId=" + investigatorId +
                ", artifactId=" + artifactId +
                '}';
    }
}
