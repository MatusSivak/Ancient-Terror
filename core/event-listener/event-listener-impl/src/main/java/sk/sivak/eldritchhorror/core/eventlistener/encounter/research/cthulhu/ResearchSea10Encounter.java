package sk.sivak.eldritchhorror.core.eventlistener.encounter.research.cthulhu;

import sk.sivak.eldritchhorror.core.constants.artifact.ArtifactId;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.location.LocationType;
import sk.sivak.eldritchhorror.core.constants.monster.NonEpicMonsterId;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.typewriter.TypewriterUtils;

import static sk.sivak.eldritchhorror.core.eventlistener.encounter.utils.EncounterUtils.getActiveInvestigatorId;

public class ResearchSea10Encounter extends AbstractCthulhuResearchEncounter {

    public ResearchSea10Encounter() {
        super(10, LocationType.SEA);
    }

    @Override
    protected void execute() {
        if (ServicePlatform.get().getConditionsDeck().hasCondition(getActiveInvestigatorId(), ConditionId.DARK_PACT)) {
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getEncounterService().typeInfo("[#GOOD]You have a Dark Pact Condition.[]");
            ServicePlatform.get().getEncounterService().typeFlavor(" \n"+getTextBuilder().withPass().withFlavor().build());
            TypewriterUtils.confirmInfos(getTextBuilder().withPass().withInfo().build()).subscribe(() -> {
                ServicePlatform.get().getService().hold();
                ServicePlatform.get().getEncounterService().finishTypewriterPaper();
                gainThisClue();
                ServicePlatform.get().getService().release();
            });
            ServicePlatform.get().getService().release();
        } else {
            TypewriterUtils.confirmInfos("[#BAD]You don't have a Dark Pact Condition.[]").subscribe(() -> {
                TypewriterUtils.confirmInfos(getTextBuilder().withFail().withInfo().build()).subscribe(() -> {
                    ServicePlatform.get().getService().hold();
                    ServicePlatform.get().getEncounterService().finishTypewriterPaper();
                    ServicePlatform.get().getMonsterService().ambush(NonEpicMonsterId.CULTIST).subscribe();
                    ServicePlatform.get().getService().release();
                });
            });
        }
    }
}
