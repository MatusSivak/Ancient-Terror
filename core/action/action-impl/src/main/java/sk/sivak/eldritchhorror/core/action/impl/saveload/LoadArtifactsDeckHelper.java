package sk.sivak.eldritchhorror.core.action.impl.saveload;

import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.constants.artifact.ArtifactInfo;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.model.save.SaveDataRead;

public class LoadArtifactsDeckHelper {

    public static void loadArtifactsDeck(SaveDataRead saveData ) {
        ServicePlatform.get().getArtifactsDeck().load(saveData.getArtifactsDeck());
        if (saveData.getModel().getInvestigatorCount() == 1) {
            ServicePlatform.get().getArtifactsDeck().removeTeamworkArtifacts();
        }
        for (String investigatorIdAsString : saveData.getArtifactsDeck().getInvestigatorArtifactsMap().keySet()) {
            InvestigatorId investigatorId = InvestigatorId.valueOf(investigatorIdAsString);
            for (ArtifactInfo artifactInfo : ServicePlatform.get().getArtifactsDeck().getArtifacts(investigatorId)) {
                ServicePlatform.get().getArtifactListenerProvider().getArtifactListener(artifactInfo.getId()).justLoadWithOwner(investigatorId, artifactInfo);
                if (artifactInfo.isDisabled()) {
                    ServicePlatform.get().getArtifactListenerProvider().getArtifactListener(artifactInfo.getId()).disable();
                }
            }
        }
    }
}
