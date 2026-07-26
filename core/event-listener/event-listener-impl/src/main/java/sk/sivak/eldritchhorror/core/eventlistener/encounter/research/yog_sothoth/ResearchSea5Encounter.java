package sk.sivak.eldritchhorror.core.eventlistener.encounter.research.yog_sothoth;

import sk.sivak.eldritchhorror.core.constants.artifact.ArtifactId;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.location.LocationType;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestPassFlavorInfoFailFlavorInfoTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.typewriter.TypewriterUtils;

import static sk.sivak.eldritchhorror.core.eventlistener.encounter.utils.EncounterUtils.getActiveInvestigatorId;

public class ResearchSea5Encounter extends AbstractYogSothothResearchEncounter {

    public ResearchSea5Encounter() {
        super(5, LocationType.SEA);
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
            ServicePlatform.get().getEncounterService().typeFlavor(" \n"+ getTextBuilder().withPass().withFlavor().build());
            TypewriterUtils.confirmInfos(
                    getTextBuilder().withPass().withInfo(1).build(),
                    getTextBuilder().withPass().withInfo(2).build()).subscribe(() -> {
                ServicePlatform.get().getService().hold();
                ServicePlatform.get().getEncounterService().finishTypewriterPaper();
                ServicePlatform.get().getGameService().gainCondition(ConditionId.DARK_PACT);
                gainThisClue();
                ServicePlatform.get().getTokenService().gainClueFromPool();
                ServicePlatform.get().getService().release();
            });
            ServicePlatform.get().getService().release();
        });
    }

    private void failEffect() {
        ServicePlatform.get().getService().hold();
        ServicePlatform.get().getEncounterService().finishTypewriterPaper();
        ServicePlatform.get().getService().hideBackground();
        ServicePlatform.get().getGameService().spawnGates(1,1).subscribe();
        ServicePlatform.get().getService().release();
    }
}
