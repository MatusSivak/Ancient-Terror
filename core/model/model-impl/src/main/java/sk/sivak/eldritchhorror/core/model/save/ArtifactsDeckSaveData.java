package sk.sivak.eldritchhorror.core.model.save;

import sk.sivak.eldritchhorror.core.constants.artifact.ArtifactId;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ArtifactsDeckSaveData implements SaveDataWrite.ArtifactsDeckSaveDataWrite {
    private List<ArtifactId> discarded = new LinkedList<>();
    private Map<String, List<? extends OwnedArtifactSaveDataRead>> investigatorArtifactsMap = new HashMap<>();

    public void setDiscarded(List<ArtifactId> discarded) {
        this.discarded = discarded;
    }

    public void setInvestigatorArtifactsMap(Map<String, List<? extends OwnedArtifactSaveDataRead>> investigatorArtifactsMap) {
        this.investigatorArtifactsMap = investigatorArtifactsMap;
    }

    @Override
    public List<ArtifactId> getDiscarded() {
        return discarded;
    }

    @Override
    public Map<String, List<? extends OwnedArtifactSaveDataRead>> getInvestigatorArtifactsMap() {
        return investigatorArtifactsMap;
    }

    public static class OwnedArtifactSaveData implements SaveDataWrite.ArtifactsDeckSaveDataWrite.OwnedArtifactSaveDataWrite {
        private ArtifactId artifactId;
        private boolean disabled;

        @Override
        public ArtifactId getArtifactId() {
            return artifactId;
        }

        public void setArtifactId(ArtifactId artifactId) {
            this.artifactId = artifactId;
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