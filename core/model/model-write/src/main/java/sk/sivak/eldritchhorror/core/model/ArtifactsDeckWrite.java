package sk.sivak.eldritchhorror.core.model;

import sk.sivak.eldritchhorror.core.constants.artifact.ArtifactId;
import sk.sivak.eldritchhorror.core.constants.artifact.ArtifactInfo;
import sk.sivak.eldritchhorror.core.constants.asset.AssetId;
import sk.sivak.eldritchhorror.core.constants.asset.AssetInfo;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.model.save.ArtifactsDeckSaveDataRead;
import sk.sivak.eldritchhorror.core.model.save.SaveDataWrite;

import java.util.List;

/**
 * @author msivak
 */
public interface ArtifactsDeckWrite extends ArtifactsDeckRead {

    void createDeck();

    ArtifactInfo removeFromDeck(ArtifactId artifactId);

    void removeTeamworkArtifacts();

    ArtifactInfo gainRandomArtifact();

    boolean removeFromDiscardPile(ArtifactInfo artifactInfo);

    void addToInvestigator(InvestigatorId investigatorId, ArtifactInfo artifactInfo);

    void discard(InvestigatorId investigatorId, ArtifactInfo artifactInfo);

    void disable(ArtifactInfo artifactInfo);

    void changeOwner(ArtifactInfo cardInfo, InvestigatorId newOwner);

    void enable(ArtifactInfo artifactInfo);

    void unlockCard(ArtifactId unlockedCard);

    SaveDataWrite.ArtifactsDeckSaveDataWrite save();

    void load(ArtifactsDeckSaveDataRead artifactsDeckSaveData);
}
