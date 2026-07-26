package sk.sivak.eldritchhorror.core.model;

import sk.sivak.eldritchhorror.core.constants.artifact.ArtifactId;
import sk.sivak.eldritchhorror.core.constants.artifact.ArtifactInfo;
import sk.sivak.eldritchhorror.core.constants.asset.AssetTrait;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;

import java.util.List;

/**
 * @author msivak
 */
public interface ArtifactsDeckRead {

    List<ArtifactId> getLockedArtifacts();

    List<ArtifactInfo> getDiscardPile();

    List<ArtifactInfo> getArtifacts(InvestigatorId investigatorId);

    boolean hasArtifact(InvestigatorId investigatorId, ArtifactId artifactId);

    ArtifactInfo findFirst(AssetTrait trait);

    ArtifactInfo findFirst(ArtifactId artifactId);

    boolean isInDeckOrDiscardPile(ArtifactId artifactId);
}
