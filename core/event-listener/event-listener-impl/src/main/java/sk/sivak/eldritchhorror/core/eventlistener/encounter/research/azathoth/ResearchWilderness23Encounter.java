package sk.sivak.eldritchhorror.core.eventlistener.encounter.research.azathoth;

import sk.sivak.eldritchhorror.core.constants.artifact.ArtifactId;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.constants.location.LocationType;
import sk.sivak.eldritchhorror.core.constants.omen.OmenId;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestPassFlavorInfoFailFlavorInfoTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.typewriter.TypewriterUtils;

import static sk.sivak.eldritchhorror.core.eventlistener.encounter.utils.EncounterUtils.getActiveInvestigatorId;

public class ResearchWilderness23Encounter extends AbstractAzathothResearchEncounter {

    public ResearchWilderness23Encounter() {
        super(23, LocationType.WILDERNESS);
    }

    @Override
    protected void execute() {
        TypewriterUtils.noYesQuestion(getTextBuilder().withQuestion().build(), this::onNo, () -> {
            if (ServicePlatform.get().getConditionsDeck().hasCondition(getActiveInvestigatorId(), ConditionId.DARK_PACT)) {
                TypewriterUtils.confirmInfos("You can't gain another Dark Pact").subscribe(() -> {
                    onNo();
                });
                return;
            }
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getEncounterService().finishTypewriterPaper();
            ServicePlatform.get().getGameService().gainCondition(ConditionId.DARK_PACT);
            gainThisClue();
            ServicePlatform.get().getService().release();
        });
    }

    private void onNo() {
        ServicePlatform.get().getService().hold();
        ServicePlatform.get().getEncounterService().typeFlavor(" \n" +getTextBuilder().withFail().withFlavor().build());
        TypewriterUtils.confirmInfos(getTextBuilder().withFail().withInfo().build()).subscribe(() -> {
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getEncounterService().finishTypewriterPaper();
            ServicePlatform.get().getDoomOmenService().addTokenToOmenTrack(OmenId.NORTH);
            ServicePlatform.get().getService().release();
        });
        ServicePlatform.get().getService().release();
    }
}
