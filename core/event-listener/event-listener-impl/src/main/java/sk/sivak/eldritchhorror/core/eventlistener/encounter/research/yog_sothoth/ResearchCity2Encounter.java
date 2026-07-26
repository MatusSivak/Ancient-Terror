package sk.sivak.eldritchhorror.core.eventlistener.encounter.research.yog_sothoth;

import sk.sivak.eldritchhorror.core.constants.artifact.ArtifactId;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.location.LocationType;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.typewriter.TypewriterUtils;

import static sk.sivak.eldritchhorror.core.eventlistener.encounter.utils.EncounterUtils.getActiveInvestigatorId;

public class ResearchCity2Encounter extends AbstractYogSothothResearchEncounter{

    public ResearchCity2Encounter() {
        super(2, LocationType.CITY);
    }

    @Override
    protected void execute() {
        TypewriterUtils.noYesQuestion(getTextBuilder().withQuestion().build(), () -> {
            TypewriterUtils.confirmInfos(" \n" + getTextBuilder().withFail().withInfo().build()).subscribe( () -> {
                failEffect();
            });
        }, () -> {
            if (ServicePlatform.get().getConditionsDeck().hasCondition(getActiveInvestigatorId(), ConditionId.DARK_PACT)) {
                TypewriterUtils.confirmInfos("You can't gain another Dark Pact.\n \n" + getTextBuilder().withFail().withInfo().build()).subscribe(() -> {
                    failEffect();
                });
                return;
            }
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getEncounterService().finishTypewriterPaper();
            ServicePlatform.get().getGameService().gainCondition(ConditionId.DARK_PACT);
            gainThisClue();
            ServicePlatform.get().getGameService().gainArtifact(ArtifactId.NECRONOMICON);
            ServicePlatform.get().getService().release();
        });
    }

    private void failEffect() {
        ServicePlatform.get().getService().hold();
        ServicePlatform.get().getEncounterService().finishTypewriterPaper();
        ServicePlatform.get().getService().hideBackground();
        ServicePlatform.get().getTokenService().discardClue(getLocationId());
        ServicePlatform.get().getService().release();
    }
}
