package sk.sivak.eldritchhorror.core.eventlistener.encounter.research.yog_sothoth;

import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.location.LocationType;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.typewriter.TypewriterUtils;

import static sk.sivak.eldritchhorror.core.eventlistener.encounter.utils.EncounterUtils.getActiveInvestigatorId;

public class ResearchSea16Encounter extends AbstractYogSothothResearchEncounter {

    public ResearchSea16Encounter() {
        super(16, LocationType.SEA);
    }

    @Override
    protected void execute() {
        boolean isCorrupted = ServicePlatform.get().getConditionsDeck().hasCondition(getActiveInvestigatorId(), ConditionId.DARK_PACT);

        ServicePlatform.get().getService().hold();
        if (isCorrupted) {
            ServicePlatform.get().getEncounterService().typeInfo("[#BAD]You have a Dark Pact Condition.[]");
            ServicePlatform.get().getEncounterService().typeFlavor(" \n" + getTextBuilder().withFail().withFlavor().build());
            TypewriterUtils.confirmInfos(getTextBuilder().withFail().withInfo().build()).subscribe(() -> {
                ServicePlatform.get().getService().hold();
                ServicePlatform.get().getEncounterService().finishTypewriterPaper();
                ServicePlatform.get().getGameService().gainCondition(ConditionId.LOST_IN_TIME_AND_SPACE);
                ServicePlatform.get().getService().release();
            });
        } else {
            ServicePlatform.get().getEncounterService().typeInfo("[#GOOD]You don't have a Dark Pact Condition.[]");
            TypewriterUtils.confirmInfos(" \n" + getTextBuilder().withPass().withInfo().build()).subscribe(() -> {
                ServicePlatform.get().getService().hold();
                ServicePlatform.get().getEncounterService().finishTypewriterPaper();
                gainThisClue();
                ServicePlatform.get().getService().release();
            });
        }
        ServicePlatform.get().getService().release();
    }
}
