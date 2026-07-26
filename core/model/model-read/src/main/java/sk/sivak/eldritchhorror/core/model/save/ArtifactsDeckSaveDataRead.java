package sk.sivak.eldritchhorror.core.model.save;

import sk.sivak.eldritchhorror.core.constants.artifact.ArtifactId;
import sk.sivak.eldritchhorror.core.constants.asset.AssetId;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;

import java.util.List;
import java.util.Map;

public interface ArtifactsDeckSaveDataRead {

    List<ArtifactId> getDiscarded();

    Map<String, List<? extends OwnedArtifactSaveDataRead>> getInvestigatorArtifactsMap();

    interface OwnedArtifactSaveDataRead {
        ArtifactId getArtifactId();

        boolean isDisabled();
    }
}
